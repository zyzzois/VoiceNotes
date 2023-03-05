package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private val repository: NoteListRepository) {

    suspend operator fun invoke(note: NoteEntity) {
        repository.deleteNote(note)
    }

}