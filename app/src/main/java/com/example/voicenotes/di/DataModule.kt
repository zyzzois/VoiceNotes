package com.example.voicenotes.di

import android.app.Application
import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.database.NoteDao
import com.example.data.repository.AuthorizationRepositoryImpl
import com.example.data.repository.NoteListRepositoryImpl
import com.example.domain.repository.AuthorizationRepository
import com.example.domain.repository.NoteListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindNoteListRepository(impl: NoteListRepositoryImpl): NoteListRepository

    @ApplicationScope
    @Binds
    fun bindAuthorizationRepository(impl: AuthorizationRepositoryImpl): AuthorizationRepository

    companion object {

        @ApplicationScope
        @Provides
        fun provideShopListDao(
            application: Application,
        ): NoteDao {
            return AppDatabase.getInstance(application).notesDao()
        }

    }
}