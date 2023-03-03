package com.example.data.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "voiceNotes")
data class NoteModelDb(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var filename: String,
    var filePath: String,
    var timestamp: String,
    var duration: String,
    var ampsPath: String
) {
    @Ignore
    var isChecked = false
}
