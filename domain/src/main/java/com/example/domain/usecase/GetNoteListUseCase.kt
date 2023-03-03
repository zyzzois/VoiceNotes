package com.example.domain.usecase

import androidx.lifecycle.LiveData
import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class GetNoteListUseCase @Inject constructor(private val repository: NoteListRepository) {

    operator fun invoke(): LiveData<List<NoteEntity>> {
        return repository.getNoteList()
    }

}