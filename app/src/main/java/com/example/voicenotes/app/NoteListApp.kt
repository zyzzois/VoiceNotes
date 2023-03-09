package com.example.voicenotes.app

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import com.example.voicenotes.di.DaggerApplicationComponent
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class NoteListApp(): Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() { }
    }

}