package com.example.domain.usecase

import com.example.domain.entity.firebase.NetworkResult
import com.example.domain.entity.firebase.FirebaseUserEntity
import com.example.domain.repository.AuthorizationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpFirebaseUseCase @Inject constructor(private val repository: AuthorizationRepository) {
    suspend operator fun invoke(user: FirebaseUserEntity): Flow<NetworkResult<Boolean>> {
        return repository.signUpFirebase(user)
    }
}