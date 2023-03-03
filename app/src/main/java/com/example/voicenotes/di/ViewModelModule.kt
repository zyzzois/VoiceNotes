package com.example.voicenotes.di

import androidx.lifecycle.ViewModel
import com.example.voicenotes.vm.MainViewModel
import com.example.voicenotes.vm.NoteItemViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(NoteItemViewModel::class)
    fun bindShopItemViewModel(viewModel: NoteItemViewModel): ViewModel

}