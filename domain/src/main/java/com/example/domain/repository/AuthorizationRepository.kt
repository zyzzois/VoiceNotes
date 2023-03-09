package com.example.domain.repository

import com.example.domain.entity.firebase.NetworkResult
import com.example.domain.entity.firebase.FirebaseUserEntity
import kotlinx.coroutines.flow.Flow

interface AuthorizationRepository {
    suspend fun signUpFirebase(user: FirebaseUserEntity): Flow<NetworkResult<Boolean>>
    suspend fun signInFirebase(mail: String, password: String): Flow<NetworkResult<Boolean>>
}