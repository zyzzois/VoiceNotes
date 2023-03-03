package com.example.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM voiceNotes")
    fun getVoiceNoteList(): LiveData<List<NoteModelDb>>

    @Query("SELECT * FROM voiceNotes WHERE id=:noteId LIMIT 1")
    suspend fun getShopItem(noteId: Int): NoteModelDb

    @Insert
    suspend fun insertNewVoiceNote(voiceNote: NoteModelDb)

    @Delete
    suspend fun deleteVoiceNote(voiceNotes: List<NoteModelDb>)

    @Update
    suspend fun update(voiceNote: NoteModelDb)
}