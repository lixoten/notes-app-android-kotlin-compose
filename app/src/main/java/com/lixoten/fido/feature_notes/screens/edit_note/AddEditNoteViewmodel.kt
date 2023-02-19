package com.lixoten.fido.feature_notes.screens.edit_note

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lixoten.fido.NotesApplication
import com.lixoten.fido.feature_notes.data.NotesRepository
import com.lixoten.fido.feature_notes.model.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddEditNoteViewmodel(
    savedStateHandle: SavedStateHandle,
    val notesRepository: NotesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditNoteUiState())
    val uiState = _uiState.asStateFlow()

    //private val _note by mutableStateOf(Note())
    //var noteModel by mutableStateOf(Note(0, "","",0,false,false))

    /**
    * Hold deleted note
    */
    private var deletedNote: Note? = null

    // what is eventFlow for? Events that are not really state..One time events like snackbar
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    //var code by mutableStateOf("")
    init {
        //val idArgument: Int = savedStateHandle[EditNoteScreenDestination.routeArg] ?: 0
        //val idArgument: Int = savedStateHandle[EditNoteScreenDestination.route] ?: 0
        //val idArgument  = savedStateHandle.get<Int>("id")!!
        //if (idArgument != -1) {
        savedStateHandle.get<Int>("id")?.let { idArgument ->
            if (idArgument != -1) {
                viewModelScope.launch {
                    //noteModel = notesRepository.getNoteById(idArgument)
                    _uiState.update {
                        it.copy(
                            note =  notesRepository.getNoteById(idArgument),
                            //title = noteModel.title,
                            //isChecked = noteModel.isChecked
                        )
                    }
                }
            }
        }
    }


    fun updateStateTitle(title: String) {
        _uiState.update {
            it.copy(
                note = _uiState.value.note.copy(
                    title = title,
                ),
                //title = title,
                dataHasChanged = true,
                titleError = (title.length < 2)
            )
        }
    }
    fun updateStateContent(content: String) {
        _uiState.update {
            it.copy(
                note = _uiState.value.note.copy(
                    content = content,
                ),
                //content = content,
                dataHasChanged = true,
                //error = (title.length < 2)
            )
        }
    }
    fun updateStateColor(cl: Int) {
        _uiState.update {
            it.copy(
                note = _uiState.value.note.copy(
                    color = cl,
                ),
                //color = cl,
                dataHasChanged = true,
                //error = (title.length < 2)
            )
        }
    }
    fun updateStateCheck(ck: Boolean) {
        _uiState.update {
            it.copy(
                note = _uiState.value.note.copy(
                    isChecked = ck,
                ),
                //isChecked = ck,
                dataHasChanged = true,
            )
        }
    }
    fun updateStatePinned() {
        _uiState.update {
            it.copy(
                note = _uiState.value.note.copy(
                    isPinned = !uiState.value.note.isPinned,
                ),
                //isPinned = !uiState.value.note.isPinned,
                dataHasChanged = true,
            )
        }
    }

    fun updateDbNote() {
        viewModelScope.launch {
            notesRepository.insertNote(
                uiState.value.note
//                uiState.value.note.copy(
//                    title = uiState.value.title,
//                    content = uiState.value.content,
//                    color = uiState.value.color,
//                    isChecked = uiState.value.isChecked,
//                    isPinned = uiState.value.isPinned,
//                    createdDate = System.currentTimeMillis(),
//                    lastMaintDate = System.currentTimeMillis(),
//                )
            )
        }
    }

    fun removeDbNote(item: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(item)
        }
    }





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
                AddEditNoteViewmodel(
                    this.createSavedStateHandle(),
                    notesRepository = notesRepository
                )
            }
        }
    }


    sealed class UiEvent{
        data class ShowSnackbar(val message: String): UiEvent()
        //object SaveNote: UiEvent()
    }

}