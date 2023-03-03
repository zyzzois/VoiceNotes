package com.example.voicenotes.app

import android.app.Application
import com.example.voicenotes.di.DaggerApplicationComponent

class NoteListApp: Application() {
    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}