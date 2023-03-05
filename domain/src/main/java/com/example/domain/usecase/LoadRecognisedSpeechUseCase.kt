package com.example.domain.usecase

import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class LoadRecognisedSpeechUseCase @Inject constructor(
    private val repository: NoteListRepository
) {
    suspend operator fun invoke(byteArray: ByteArray) {
        repository.loadRecognisedSpeechFromServer(byteArray)
    }
}