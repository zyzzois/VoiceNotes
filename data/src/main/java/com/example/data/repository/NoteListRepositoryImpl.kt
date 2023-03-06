package com.example.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.data.database.NoteDao
import com.example.data.mapper.Mapper
import com.example.domain.entity.NetworkResult
import com.example.domain.entity.NoteEntity
import com.example.domain.entity.UserModel
import com.example.domain.repository.NoteListRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class NoteListRepositoryImpl @Inject constructor(
    private val noteListDao: NoteDao,
    private val mapper: Mapper,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
): NoteListRepository {

    override suspend fun addNote(noteEntity: NoteEntity) {
        noteListDao.insertNewVoiceNote(mapper.mapEntityToDbModel(noteEntity))
    }

    override suspend fun deleteNote(note: NoteEntity) {
        noteListDao.deleteVoiceNote(mapper.mapEntityToDbModel(note))
    }

    override suspend fun getNote(noteId: Int): NoteEntity {
        val dbModel = noteListDao.getShopItem(noteId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun searchInDatabase(query: String): List<NoteEntity> {
        val searchedList = noteListDao.searchInDatabase(query)
        return searchedList.map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override suspend fun signUpFirebase(user: UserModel): Flow<NetworkResult<Boolean>> {
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

    override fun getNoteList(): LiveData<List<NoteEntity>> = Transformations.map(
        noteListDao.getVoiceNoteList()
    ) {
        mapper.mapListDbModelToList(it)
    }

    companion object {
        private const val SOMETHING_WENT_WRONG = "Что-то пошло не так"
        private const val TAG = "firebaseAuth"
        private const val RES_SIGN_UP_SUCCESS = "user with email and password successfully created"
        private const val RES_SIGN_IN_SUCCESS = "successfully signed up"
        private const val REC_SIGN_UP_FAILED = "user with email and password successfully does not created"
        private const val REC_SIGN_IN_FAILED = "failed to sign in"
        private const val USERS = "users"
        private const val DOC_UPLOADING_FAILED = "Error adding document"
        private const val DOC_UPLOADING_SUCCESS = "DocumentSnapshot added with ID:"
    }
}