package com.example.voicenotes.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.NoteEntity
import com.example.domain.usecase.AddNoteItemUseCase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.measureTime

class NoteItemViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase
): ViewModel() {

    fun addNoteItem(fileName: String, timesTamp: String, duration: Long, filePath: String) {
        viewModelScope.launch {
            val noteItem = NoteEntity(
                fileName = fileName,
                timesTamp = timesTamp,
                duration = duration.toString(),
                filepath = filePath
            )
            addNoteItemUseCase(noteItem)
        }
    }

}