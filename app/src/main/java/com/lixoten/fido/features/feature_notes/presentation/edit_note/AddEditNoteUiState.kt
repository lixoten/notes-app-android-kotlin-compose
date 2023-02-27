package com.lixoten.fido.features.feature_notes.presentation.edit_note

import com.lixoten.fido.features.feature_notes.model.Note

data class AddEditNoteUiState(
    val note: Note = Note(-1,"","",0,false, false),
    val foo: String = "",
    //val title: String = "",
    val titleError: Boolean = false,
    //val content: String = "",
    //val color: Int = 0,
    //val isChecked: Boolean = false,
    //val isPinned: Boolean = false,
    val dataHasChanged: Boolean = false,
)
