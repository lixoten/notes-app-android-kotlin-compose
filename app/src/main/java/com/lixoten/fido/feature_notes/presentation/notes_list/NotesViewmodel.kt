package com.lixoten.fido.feature_notes.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lixoten.fido.NotesApplication
import com.lixoten.fido.feature_notes.data.UserPreferencesRepository
import com.lixoten.fido.feature_notes.domain.use_case.NoteUseCases
import com.lixoten.fido.feature_notes.model.Note
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewmodel(
    private val noteUseCasesWrapper: NoteUseCases,
    private val userPreferencesRepository: UserPreferencesRepository
    //val notesRepository: NotesRepository
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
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect {
                _uiState.value = uiState.value.copy(
                    isGridLayout = it.isGridLayout,
                )
                //processSearchQueryFlow(it.searchValue)
            }
        }
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


    private fun getAllDbNotes(noteOrderBy: NoteOrderBy) {
        // every time we call this function we get a new instance of that flow
        // every time we call this function we want to cancel the old coroutine that is already observing our DB
        getNotesJob?.cancel()

        getNotesJob = noteUseCasesWrapper.getAllNotes(noteOrderBy)
            // on each emission
            .onEach { notes ->
                _uiState.update {
                    uiState.value.copy(
                        notes = notes,
                        noteOrderBy = noteOrderBy
                    )
                }
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

    fun updatePreferenceLayout(newValue: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserPreferences(isGridLayout = newValue)
        }
    }


    fun onEvents(event: NotesEvents) {
        when (event) {
            is NotesEvents.ToggleLayout -> {
                _uiState.update {
                    uiState.value.copy(
                        isGridLayout = !uiState.value.isGridLayout
                    )
                }
                updatePreferenceLayout(uiState.value.isGridLayout)
            }
            is NotesEvents.RemoveDbRecord -> {
                viewModelScope.launch {
                    deletedNote = event.note

                    noteUseCasesWrapper.deleteNote(event.note)
                    //notesRepository.deleteNote(event.note)
                }
            }
            is NotesEvents.RestoreDbRecord -> {
                viewModelScope.launch {
                    noteUseCasesWrapper.addNote(deletedNote ?: return@launch)
                    deletedNote = null
                }
            }
            is NotesEvents.AddDbRecord -> {
                viewModelScope.launch {

                    val sz = uiState.value.notes.count()
//                  val tmp = formatNewRecord(record.title, newId)

                    //notesRepository.insertNote(
                    noteUseCasesWrapper.addNote(
                        Note(
                            id = -1,
                            title = "",
                            content = "",
                            color = 0,
                            isPinned = false,
                        ),
//                        Note(
//                            title = record.title,
//                            content = record.content,
//                            color = record.color,
//                            isPinned = record.isPinned,
//                        ),
                        cnt = sz
                    )
                }
            }
            is NotesEvents.UpdateStateOrderSectionIsVisible -> {
                _uiState.update {
                    uiState.value.copy(
                        isOrderSectionVisible = !uiState.value.isOrderSectionVisible
                    )
                }
            }
            is NotesEvents.ToggleSearch -> {
                _uiState.update {
                    uiState.value.copy(
                        isSearchVisible = !uiState.value.isSearchVisible
                    )
                }
                //updatePreferenceLayout(uiState.value.isGridLayout)
            }

            is NotesEvents.UpdateDbIsCheck -> {
                viewModelScope.launch {
                    noteUseCasesWrapper.updateNote(
                        note = event.note.copy(
                            isChecked = !event.note.isChecked
                        )
                    )
//                    notesRepository.updateNote(
//                        note = event.note.copy(
//                            isChecked = !event.note.isChecked
//                        )
//                    )
                }
            }
            is NotesEvents.UpdateDbIsUnpin -> {
                viewModelScope.launch {
                    noteUseCasesWrapper.updateNote(
                        note = event.note.copy(
                            isPinned = !event.note.isPinned
                        )
                    )
//                    notesRepository.updateNote(
//                        note = event.note.copy(
//                            isPinned = !event.note.isPinned
//                        )
//                    )
                }
            }
            is NotesEvents.UpdateStateNoteOrderBy -> {
                _uiState.update {
                    uiState.value.copy(
                        noteOrderBy = event.orderBy.copy(orderType = event.orderBy.orderType)
                    )
                }
                getAllDbNotes(event.orderBy)
            }
        }
    }

    /**
     * Factory for BookshelfViewModel] that takes BookshelfRepository] as a dependency
     */
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotesApplication)
                //val notesRepository = application.appContainer.notesRepository
                val noteUseCasesXXX = application.appContainer.noteUseCases
                val preferencesRepository = application.userPreferencesRepository

                NotesViewmodel(
                    //this.createSavedStateHandle(),
                    //notesRepository = notesRepository,
                    noteUseCasesWrapper = noteUseCasesXXX,
                    userPreferencesRepository = preferencesRepository
                )
            }
        }
    }
}

//private fun getNotes() =
//    List(6) {
//        Note(it, "Note # $it")
//    }