package com.example.a216839_wan_project2.data.dao

import androidx.room.*
import com.example.a216839_wan_project2.data.entity.SavedClinicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedClinicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clinic: SavedClinicEntity)

    @Delete
    suspend fun delete(clinic: SavedClinicEntity)

    @Query("SELECT * FROM saved_clinics ORDER BY savedAt DESC")
    fun getAll(): Flow<List<SavedClinicEntity>>

    // Check if a clinic name already saved (to show saved/unsaved state)
    @Query("SELECT COUNT(*) FROM saved_clinics WHERE name = :name")
    suspend fun countByName(name: String): Int
}
