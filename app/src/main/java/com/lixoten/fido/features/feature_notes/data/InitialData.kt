package com.lixoten.fido.features.feature_notes.data

import com.lixoten.fido.features.feature_notes.model.Note

object InitialData {

    fun getNotes() =
        List(6) {
            Note(it,
                title = "Note # $it",
                content = "",
                color = 0,
                isChecked = false,
                isPinned = false,
            )
        }
}