package com.example.domain.entity

data class NoteEntity(
    val name: String,
    val id: Int = UNDEFINED_ID,
    val voice: Long
) {
    companion object {
        const val UNDEFINED_ID = 0
    }
}
