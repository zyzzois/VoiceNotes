package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository

class DeleteNoteItemUseCase(private val repository: NoteListRepository) {

    operator fun invoke(voiceNote: NoteEntity) {
        repository.deleteNote(voiceNote)
    }

}