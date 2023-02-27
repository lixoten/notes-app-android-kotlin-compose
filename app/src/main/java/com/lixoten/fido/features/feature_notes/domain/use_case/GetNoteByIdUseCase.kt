package com.lixoten.fido.features.feature_notes.domain.use_case

import com.lixoten.fido.features.feature_notes.data.NotesRepository
import com.lixoten.fido.features.feature_notes.model.Note


class GetNoteByIdUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(id: Int): Note  {
        return repository.getNoteById(id)
    }
}
