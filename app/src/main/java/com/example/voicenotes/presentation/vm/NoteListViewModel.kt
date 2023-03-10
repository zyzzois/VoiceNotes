package com.example.voicenotes.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.NoteEntity
import com.example.domain.usecase.DeleteNoteUseCase
import com.example.domain.usecase.GetNoteListUseCase
import com.example.domain.usecase.SearchInDatabaseUseCase
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
    private val deleteNoteItemUseCase: DeleteNoteUseCase,
    private val getNoteListUseCase: GetNoteListUseCase,
    private val searchInDatabaseUseCase: SearchInDatabaseUseCase,
): ViewModel() {

    val noteList = getNoteListUseCase()

    private val _searchedQueryList = MutableLiveData<List<NoteEntity>>()
    val searchedQueryList: LiveData<List<NoteEntity>>
        get() = _searchedQueryList

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            deleteNoteItemUseCase(note)
            val file = File(note.filepath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun searchInDataBase(query: String) {
        viewModelScope.launch {
            _searchedQueryList.value = searchInDatabaseUseCase(query)
        }
    }

}