package com.lixoten.fido.feature_notes.data

import com.lixoten.fido.feature_notes.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Note] from a given data source.
 */
interface NotesRepository {
    suspend fun getNoteById(id: Int): Note

    suspend fun getAllNotes(): List<Note>

    fun getAllNotesFlow(): Flow<List<Note>>

    /**
     * Insert note in the data source
     */
    suspend fun insertNote(note: Note)

    /**
     * Update note in the data source
     */
    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)


    suspend fun updateCompleted(id: Int, completed: Boolean)
}
