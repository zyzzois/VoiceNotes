package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository

class GetNotesListUseCase(private val repository: NoteListRepository) {

    operator fun invoke(): List<NoteEntity> {
        return repository.getNoteList()
    }

}