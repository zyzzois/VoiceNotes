package com.example.domain.entity


data class NoteEntity(
    val fileName: String,
    val timesTamp: String,
    val duration: String,
    var id: Int = UNDEFINED_ID,
    var filepath: String,
    var isPlaying: Boolean = false,
    var isChecked: Boolean = false
) {
    companion object{
        const val UNDEFINED_ID = 0
    }
}

