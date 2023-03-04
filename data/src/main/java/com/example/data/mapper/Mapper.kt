package com.example.data.mapper

import com.example.data.database.NoteModelDb
import com.example.domain.entity.NoteEntity
import java.text.SimpleDateFormat
import javax.inject.Inject

class Mapper @Inject constructor() {
    private val sdf = SimpleDateFormat("MM:ss")


    fun mapEntityToDbModel(noteEntity: NoteEntity) =
        NoteModelDb(
            id = noteEntity.id,
            filename = noteEntity.fileName,
            filePath = noteEntity.filepath,
            timestamp = noteEntity.timesTamp,
            duration = formatMillis(noteEntity.duration),
            ampsPath = ""
         )


    fun mapDbModelToEntity(noteModelDb: NoteModelDb) =
        NoteEntity(
            fileName = noteModelDb.filename,
            timesTamp = noteModelDb.timestamp,
            duration = noteModelDb.duration,
            id = noteModelDb.id,
            filepath = noteModelDb.filePath
        )

    fun mapListDbModelToList(list: List<NoteModelDb>) = list.map {
        mapDbModelToEntity(it)
    }


    private fun formatMillis(millis: String): String {
        val tmp = millis.toLongOrNull()
        return sdf.format(tmp)
    }


}