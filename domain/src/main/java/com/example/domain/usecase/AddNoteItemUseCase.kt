package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository

class AddNoteItemUseCase(private val repository: NoteListRepository) {

    operator fun invoke(note: NoteEntity) {
        repository.addNote(note)
    }

}