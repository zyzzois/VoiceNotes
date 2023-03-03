package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(private val repository: NoteListRepository) {
    suspend operator fun invoke(noteId: Int): NoteEntity {
        return repository.getNote(noteId)
    }
}