package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.data.database.NoteDao
import com.example.data.mapper.Mapper
import com.example.domain.entity.NoteEntity
import com.example.domain.repository.NoteListRepository
import javax.inject.Inject

class NoteListRepositoryImpl @Inject constructor(
    private val noteListDao: NoteDao,
    private val mapper: Mapper,
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

    override fun getNoteList(): LiveData<List<NoteEntity>> = Transformations.map(
        noteListDao.getVoiceNoteList()
    ) {
        mapper.mapListDbModelToList(it)
    }

}