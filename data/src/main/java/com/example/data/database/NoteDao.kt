package com.example.data.database

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM voiceNotes")
    suspend fun getVoiceNoteList(): List<NoteModelDb>

    @Insert
    suspend fun insertNewVoiceNote(vararg voiceNote: NoteModelDb)

    @Delete
    suspend fun deleteVoiceNote(voiceNotes: Array<NoteModelDb>)

    @Update
    fun update(voiceNote: NoteModelDb)
}