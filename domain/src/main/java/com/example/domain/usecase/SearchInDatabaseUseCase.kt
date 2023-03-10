package com.example.domain.usecase

import androidx.lifecycle.LiveData
import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class SearchInDatabaseUseCase @Inject constructor(private val repository: NoteListRepository) {
    suspend operator fun invoke(query: String): List<NoteEntity> {
        return repository.searchInDatabase(query)
    }
}