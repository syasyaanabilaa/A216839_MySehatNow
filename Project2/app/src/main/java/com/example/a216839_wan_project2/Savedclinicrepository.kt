package com.example.a216839_wan_project2.data.repository

import com.example.a216839_wan_project2.data.dao.SavedClinicDao
import com.example.a216839_wan_project2.data.entity.SavedClinicEntity
import kotlinx.coroutines.flow.Flow

class SavedClinicRepository(private val dao: SavedClinicDao) {
    val allSaved: Flow<List<SavedClinicEntity>> = dao.getAll()
    suspend fun insert(clinic: SavedClinicEntity) = dao.insert(clinic)
    suspend fun delete(clinic: SavedClinicEntity) = dao.delete(clinic)
    suspend fun isSaved(name: String): Boolean    = dao.countByName(name) > 0
}
