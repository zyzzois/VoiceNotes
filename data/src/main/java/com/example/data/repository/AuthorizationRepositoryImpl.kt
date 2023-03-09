package com.example.data.repository

import android.util.Log
import androidx.activity.ComponentActivity
import com.example.domain.entity.firebase.FirebaseUserEntity
import com.example.domain.entity.firebase.NetworkResult
import com.example.domain.repository.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
): AuthorizationRepository {

    override suspend fun signUpFirebase(user: FirebaseUserEntity): Flow<NetworkResult<Boolean>> {
        return flow {
            var isSuccess = false
            emit(NetworkResult.Loading())

            try {
                firebaseAuth.createUserWithEmailAndPassword(user.login, user.password)
                    .addOnCompleteListener {
                        isSuccess = if (it.isSuccessful) {
                            Log.d(TAG, RES_SIGN_UP_SUCCESS)
                            val firebaseUser = firebaseAuth.currentUser
                            if (firebaseUser != null) {
                                user.userId = firebaseUser.uid
                                fireStore
                                    .collection(USERS)
                                    .add(user)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG,  DOC_UPLOADING_SUCCESS + documentReference.id)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, DOC_UPLOADING_FAILED, e)
                                    }
                            }
                            firebaseUser != null
                        } else {
                            Log.d(TAG, REC_SIGN_UP_FAILED, it.exception)
                            false
                        }
                    }.await()

                if (isSuccess) {
                    emit(NetworkResult.Success(true))
                } else {
                    emit(NetworkResult.Error(SOMETHING_WENT_WRONG))
                }
            } catch (error: Exception) {
                emit(NetworkResult.Error(message = error.localizedMessage ?: SOMETHING_WENT_WRONG))
            }
        }
    }

    override suspend fun signInFirebase(
        mail: String,
        password: String,
    ): Flow<NetworkResult<Boolean>> {
        return flow {
            var isSuccess = false
            emit(NetworkResult.Loading())

            try {
                firebaseAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener {
                        isSuccess = if (it.isSuccessful) {
                            Log.d(TAG, RES_SIGN_IN_SUCCESS)
                            firebaseAuth.currentUser != null
                        } else {
                            Log.d(TAG, REC_SIGN_IN_FAILED, it.exception)
                            false
                        }
                    }

                if (isSuccess) {
                    emit(NetworkResult.Success(true))
                } else {
                    emit(NetworkResult.Error(SOMETHING_WENT_WRONG))
                }
            } catch (error: Exception) {
                emit(NetworkResult.Error(message = error.localizedMessage ?: SOMETHING_WENT_WRONG))
            }

        }
    }

    companion object {
        private const val TAG = "Authorization"
        private const val RES_SIGN_UP_SUCCESS = "user with email and password successfully created"
        private const val RES_SIGN_IN_SUCCESS = "successfully signed up"
        private const val REC_SIGN_UP_FAILED = "user with email and password successfully does not created"
        private const val REC_SIGN_IN_FAILED = "failed to sign in"
        private const val USERS = "users"
        private const val SOMETHING_WENT_WRONG = "Что-то пошло не так"
        private const val DOC_UPLOADING_FAILED = "Error adding document"
        private const val DOC_UPLOADING_SUCCESS = "DocumentSnapshot added with ID:"
    }
}