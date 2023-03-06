package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.entity.NetworkResult
import com.example.domain.entity.NoteEntity
import com.example.domain.entity.UserModel
import kotlinx.coroutines.flow.Flow

interface NoteListRepository {
    suspend fun addNote(noteEntity: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun getNote(noteId: Int): NoteEntity
    suspend fun searchInDatabase(query: String): List<NoteEntity>
    suspend fun signUpFirebase(user: UserModel): Flow<NetworkResult<Boolean>>
    suspend fun signInFirebase(mail: String, password: String): Flow<NetworkResult<Boolean>>
    fun getNoteList(): LiveData<List<NoteEntity>>
}