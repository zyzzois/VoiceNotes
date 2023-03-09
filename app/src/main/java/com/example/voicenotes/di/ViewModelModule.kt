package com.example.voicenotes.di

import androidx.lifecycle.ViewModel
import com.example.voicenotes.presentation.vm.AuthorizationViewModel
import com.example.voicenotes.presentation.vm.NoteListViewModel
import com.example.voicenotes.presentation.vm.NoteItemViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(NoteListViewModel::class)
    fun bindNoteListViewModel(viewModel: NoteListViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(NoteItemViewModel::class)
    fun bindNoteItemViewModel(viewModel: NoteItemViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(AuthorizationViewModel::class)
    fun bindAuthorizationViewModel(viewModel: AuthorizationViewModel): ViewModel

}