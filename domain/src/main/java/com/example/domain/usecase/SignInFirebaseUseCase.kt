package com.example.domain.usecase

import com.example.domain.entity.NetworkResult
import com.example.domain.repository.NoteListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInFirebaseUseCase @Inject constructor(private val repository: NoteListRepository) {
    suspend operator fun invoke(mail: String, password: String): Flow<NetworkResult<Boolean>> {
        return repository.signInFirebase(mail, password)
    }
}