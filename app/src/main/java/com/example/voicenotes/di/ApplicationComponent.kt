package com.example.voicenotes.di

import android.app.Application
import com.example.voicenotes.presentation.screens.MainActivity
import com.example.voicenotes.presentation.screens.NotesActivity
import com.example.voicenotes.presentation.screens.PlayerActivity

import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: NotesActivity)
    fun inject(activity: PlayerActivity)

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }

}