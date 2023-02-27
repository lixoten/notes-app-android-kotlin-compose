package com.lixoten.fido.feature_notes.data

import androidx.room.*
import com.lixoten.fido.feature_notes.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query(
        """
            SELECT * FROM notes ORDER BY id DESC
        """
    )
    fun getAllNotesFlow(): Flow<List<Note>>

    @Query(
        """
            SELECT * FROM notes ORDER BY id DESC
        """
    )
    suspend fun getAllNotes(): List<Note>


    @Query(
        """
            SELECT * FROM notes WHERE id = :id
        """
    )
    suspend fun getNoteById(id: Int): Note
    //suspend fun getTodoById(id: Int): TodoEntity?

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Note into the database Room ignores the conflict.
    //@Insert(onConflict = OnConflictStrategy.IGNORE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("UPDATE notes SET checked = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Int, completed: Boolean)
}