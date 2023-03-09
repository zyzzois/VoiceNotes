package com.example.domain.usecase

import com.example.domain.entity.firebase.NetworkResult
import com.example.domain.repository.AuthorizationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInFirebaseUseCase @Inject constructor(private val repository: AuthorizationRepository) {
    suspend operator fun invoke(mail: String, password: String): Flow<NetworkResult<Boolean>> {
        return repository.signInFirebase(mail, password)
    }
}