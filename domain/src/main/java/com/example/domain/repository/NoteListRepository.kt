package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.entity.NoteEntity

interface NoteListRepository {
    suspend fun addNote(noteEntity: NoteEntity)
    suspend fun deleteNotes(noteList: List<NoteEntity>)
    suspend fun getNote(noteId: Int): NoteEntity
    suspend fun searchInDatabase(query: String): List<NoteEntity>
    fun getNoteList(): LiveData<List<NoteEntity>>
}