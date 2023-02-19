package com.lixoten.fido.di

import android.content.Context
import com.lixoten.fido.feature_notes.data.NotesDatabase
import com.lixoten.fido.feature_notes.data.NotesRepository
import com.lixoten.fido.feature_notes.data.OfflineNotesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val notesRepository: NotesRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineNotesRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {

    /**
     * Implementation for [NotesRepository]
     */
    override val notesRepository: NotesRepository by lazy {
        OfflineNotesRepository(NotesDatabase.getDatabase(context).notesDao())
    }
}