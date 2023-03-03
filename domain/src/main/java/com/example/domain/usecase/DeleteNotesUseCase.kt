package com.example.domain.usecase

import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(private val repository: NoteListRepository) {

    suspend operator fun invoke(noteList: List<NoteEntity>) {
        repository.deleteNotes(noteList)
    }

}