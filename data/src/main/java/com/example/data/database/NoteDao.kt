package com.example.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM voiceNotes")
    fun getVoiceNoteList(): LiveData<List<NoteModelDb>>

    @Query("SELECT * FROM voiceNotes WHERE id=:noteId LIMIT 1")
    suspend fun getShopItem(noteId: Int): NoteModelDb

    @Query("SELECT * FROM voiceNotes WHERE filename LIKE :query")
    suspend fun searchInDatabase(query: String): List<NoteModelDb>

    @Insert
    suspend fun insertNewVoiceNote(note: NoteModelDb)

    @Delete
    suspend fun deleteVoiceNote(note: NoteModelDb)

    @Update
    suspend fun update(note: NoteModelDb)
}