package com.example.data.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "voiceNotes")
data class NoteModelDb(
    var filename: String,
    var filePath: String,
    var timestamp: Long,
    var duration: String,
    var ampsPath: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    @Ignore
    var isChecked = false
}
