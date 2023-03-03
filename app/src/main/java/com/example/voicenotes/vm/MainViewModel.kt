package com.example.voicenotes.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.NoteEntity
import com.example.domain.usecase.DeleteNotesUseCase
import com.example.domain.usecase.GetNoteListUseCase
import com.example.domain.usecase.GetNoteUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val deleteNoteItemUseCase: DeleteNotesUseCase,
    private val getNoteListUseCase: GetNoteListUseCase,
    private val getNoteUseCase: GetNoteUseCase
): ViewModel() {

    val noteList = getNoteListUseCase()

    private val _noteItemEntity = MutableLiveData<NoteEntity>()
    val noteItemEntity: LiveData<NoteEntity>
        get() = _noteItemEntity

    private val _nowSomeNoteIsPlaying = MutableLiveData<Boolean>()
    val nowSomeNoteIsPlaying: LiveData<Boolean>
        get() = _nowSomeNoteIsPlaying



    fun deleteNotes(noteList: List<NoteEntity>) {
        viewModelScope.launch {
            deleteNoteItemUseCase(noteList)
        }
    }

    fun getNoteItemById(noteId: Int): NoteEntity? {
        viewModelScope.launch {
            val item = getNoteUseCase(noteId)
            _noteItemEntity.value = item
        }
        return noteItemEntity.value
    }



}