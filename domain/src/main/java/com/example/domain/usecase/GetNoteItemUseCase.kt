package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository

class GetNoteItemUseCase(private val repository: NoteListRepository) {

    operator fun invoke(noteItemId: Int): NoteEntity {
        return repository.getNote(noteItemId)
    }

}