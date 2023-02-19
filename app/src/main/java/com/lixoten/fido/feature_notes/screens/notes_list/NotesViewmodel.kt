package com.lixoten.fido.feature_notes.screens.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lixoten.fido.NotesApplication
import com.lixoten.fido.feature_notes.data.NotesRepository
import com.lixoten.fido.feature_notes.model.Note
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewmodel(
    val notesRepository: NotesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.asStateFlow()

    private var getNotesJob: Job? = null

    //var inputNote by mutableStateOf("")
    //    private set

    private var deletedNote: Note? = null



    // notes this works too
//    val homeUiState: StateFlow<NotesUiState> =
//        notesRepository.getAllNotesFlow().map { NotesUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = NotesUiState()
//            )


    private val x = 2

    init {
        if (x == 1) {
//            viewModelScope.launch {
//                val noteList = notesRepository.getAllNotes()
//                _uiState.update {
//                    uiState.value.copy(
//                        notes = noteList
//                    )
//                }
//            }
        } else {
            getAllDbNotes(
                noteOrderBy = NoteOrderBy.Title(OrderType.Ascending)
            )
        }

    }

    //fun getFull(): Flow<List<Note>> = notesRepository.getAllNotesFlow()


    fun getAllDbNotes(noteOrderBy: NoteOrderBy) {
//        notesRepository.getAllNotesFlow().map { NotesUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = NotesUiState()
//            )

        getNotesJob?.cancel()

        getNotesJob = notesRepository.getAllNotesFlow().map {notes ->
            when (noteOrderBy.orderType) {
                is OrderType.Ascending -> {
                    when(noteOrderBy) {
                        is NoteOrderBy.Title -> notes.sortedBy { it.title.lowercase()}
                        is NoteOrderBy.Date ->notes.sortedBy { it.createdDate}
                        is NoteOrderBy.Color ->notes.sortedBy { it.color}
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrderBy) {
                        is NoteOrderBy.Title -> notes.sortedByDescending { it.title.lowercase()}
                        is NoteOrderBy.Date ->notes.sortedByDescending { it.createdDate}
                        is NoteOrderBy.Color ->notes.sortedByDescending { it.color}
                    }
                }
            }
        }
            // on each emission
            .onEach { result ->
                _uiState.update {
                    uiState.value.copy(
                        notes = result
                    )
                }
                // Notes: read somehere to always use .update
                //_uiState.value = uiState.value.copy(
                //    aList = result,
                //)
            }.launchIn(viewModelScope)
    }


    // #####################
    // State interactions

//    fun updateStateInput(input: String) {
//        _uiState.update {
//            uiState.value.copy(
//                inputNote = input
//            )
//        }
//    }

    fun updateStateOrderSectionIsVisible() {
        _uiState.update {
            uiState.value.copy(
                isOrderSectionVisible = !uiState.value.isOrderSectionVisible
            )
        }
    }

    fun updateStateNoteOrderBy(order: NoteOrderBy) {
        _uiState.update {
            uiState.value.copy(
                noteOrderBy = order.copy(orderType = order.orderType)
            )
        }
        getAllDbNotes(order)
    }



    // #####################
    // DataBase interactions

    fun updateDbIsCheck(record: Note) {
        viewModelScope.launch {
            notesRepository.updateNote(
                note = record.copy(
                    isChecked = !record.isChecked
                )
            )
        }
    }

    fun updateDbIsPinned(record: Note) {
        viewModelScope.launch {
            notesRepository.updateNote(
                note = record.copy(
                    isPinned = !record.isPinned
                )
            )
        }
    }

    // Notes: Works because of REPLACE
    //  - as in: @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun XXXchangeNoteChecked(record: Note) {
        viewModelScope.launch {
            notesRepository.insertNote(
                note = record.copy(
                    isChecked = !record.isChecked
                )
            )
        }
    }


    fun removeDbRecord(record: Note) {
        viewModelScope.launch {
            deletedNote = record

            notesRepository.deleteNote(record)
        }
    }

    fun restoreDbRecord() {
        viewModelScope.launch {
            notesRepository.insertNote(deletedNote ?: return@launch)
            deletedNote = null
        }
    }

    fun addDbRecord() {
        // beg before room
        // val newId: Int
        // if (uiState.value.notes.isEmpty()){
        //     newId = 1
        // } else {
        //     newId = uiState.value.notes[uiState.value.notes.lastIndex].id + 1
        // }
        //val tmp = formatNewRecord(item.name, newId)
        val record =
            Note(
                id = 0,
                title = "Untitle Note",
                content = "",
                color = 0,
                isPinned = false,
            )

        // end before room

        viewModelScope.launch {
            val newId: Int
            val sz = uiState.value.notes.count()

            if (sz == 0) {
                newId = 1
            } else {
                newId = sz + 1
            }
            val tmp = formatNewRecord(record.title, newId)

            notesRepository.insertNote(
                Note(
                    title = tmp,
                    content = record.content,
                    color = record.color,
                    isPinned = record.isPinned,
                )
            )
        }

    }

    private fun formatNewRecord(input: String, num: Int): String {
        return if (input == "") {
            "Note # $num"
        } else {
            "$input # $num"
        }
    }


    /////////////////////////////////////////
//    /**
//     * Inserts an [Item] in the Room database
//     */
//    suspend fun insertNotesss() {
//        if (validateInput()) {
//            itemsRepository.insertItem(itemEntryUiState.itemDetails.toItem())
//        }
//    }
//
//    private fun validateInput(uiState: ItemDetails = itemEntryUiState.itemDetails): Boolean {
//        return with(uiState) {
//            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
//        }
//    }


    // Notes: Question: At moment this is chuck of code is repeated in two files
    //  in QueryViewModel and in DetailsViewModel.
    //  what can I do/ place it so as not to have repeat code? I tried but I got a bunch of errors
    /**
     * Factory for BookshelfViewModel] that takes BookshelfRepository] as a dependency
     */
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotesApplication)
                val notesRepository = application.appContainer.notesRepository
                NotesViewmodel(
                    //this.createSavedStateHandle(),
                    notesRepository = notesRepository
                )
            }
        }
    }
}

//private fun getNotes() =
//    List(6) {
//        Note(it, "Note # $it")
//    }