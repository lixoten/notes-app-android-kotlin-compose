package com.lixoten.fido

import android.app.Application
import com.lixoten.fido.di.AppContainer
import com.lixoten.fido.di.AppDataContainer

class NotesApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
    }
}
