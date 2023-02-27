package com.lixoten.fido.features.feature_notes.domain.use_case

import com.lixoten.fido.features.feature_notes.data.NotesRepository
import com.lixoten.fido.features.feature_notes.model.Note

class AddNoteUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note, cnt: Int = 0) {
        val sz = cnt

        val tmpId: Int
        val tmpTitle: String
        if (note.id == -1) {

            if (sz == 0) {
                tmpId = 0
            } else {
                tmpId = sz + 1
            }
            if (note.title.isBlank())
                tmpTitle = "Untitled Note"
                //tmpTitle = formatNewRecord("Untitled Note - list", tmpId) //note.title
            else
                tmpTitle = note.title
        } else {
            tmpTitle = note.title
            tmpId = note.id
        }


        val newnite =
            note.copy(
                id = tmpId,
                title = tmpTitle
            )

        repository.insertNote(newnite)
    }

    private fun formatNewRecord(input: String, num: Int): String {
        return if (input == "") {
            "Note # $num"
        } else {
            "$input # $num"
        }
    }

}
