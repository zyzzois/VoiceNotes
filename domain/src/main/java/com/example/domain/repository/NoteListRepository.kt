package com.example.domain.repository

import com.example.domain.entity.NoteEntity


interface NoteListRepository {

    fun addNote(noteEntity: NoteEntity)

    fun deleteNote(noteEntity: NoteEntity)

    fun getNote(noteEntityId: Int): NoteEntity

    fun getNoteList(): List<NoteEntity>

}