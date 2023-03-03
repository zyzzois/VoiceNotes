package com.example.voicenotes.di

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.database.NoteDao
import com.example.data.repository.NoteListRepositoryImpl
import com.example.domain.repository.NoteListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindShopListRepository(impl: NoteListRepositoryImpl): NoteListRepository

    companion object {

        @ApplicationScope
        @Provides
        fun provideShopListDao(
            application: Application
        ): NoteDao {
            return AppDatabase.getInstance(application).notesDao()
        }

    }

}