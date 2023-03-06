package com.example.domain.usecase

import com.example.domain.entity.NetworkResult
import com.example.domain.entity.UserModel
import com.example.domain.repository.NoteListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpFirebaseUseCase @Inject constructor(private val repository: NoteListRepository) {
    suspend operator fun invoke(user: UserModel): Flow<NetworkResult<Boolean>> {
        return repository.signUpFirebase(user)
    }
}