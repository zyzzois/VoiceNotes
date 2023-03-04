package com.example.voicenotes.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.NoteEntity
import com.example.domain.usecase.DeleteNotesUseCase
import com.example.domain.usecase.GetNoteListUseCase
import com.example.domain.usecase.GetNoteUseCase
import com.example.domain.usecase.SearchInDatabaseUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val deleteNoteItemUseCase: DeleteNotesUseCase,
    private val getNoteListUseCase: GetNoteListUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val searchInDatabaseUseCase: SearchInDatabaseUseCase
): ViewModel() {

    val noteList = getNoteListUseCase()

    private val _isEditMode = MutableLiveData<Boolean>()
    val isEditMode: LiveData<Boolean>
        get() = _isEditMode

    private val _searchedQueryList = MutableLiveData<List<NoteEntity>>()
    val searchedQueryList: LiveData<List<NoteEntity>>
        get() = _searchedQueryList


    fun deleteNotes(noteList: List<NoteEntity>) {
        viewModelScope.launch {
            deleteNoteItemUseCase(noteList)
        }
    }


    fun searchInDataBase(query: String) {
        viewModelScope.launch {
            _searchedQueryList.value = searchInDatabaseUseCase(query)
        }
    }

    fun setEditMode(mode: Boolean) {
        _isEditMode.value = true
    }

}