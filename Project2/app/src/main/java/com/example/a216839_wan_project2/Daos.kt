package com.example.a216839_wan_project2.data.dao

import androidx.room.*
import com.example.a216839_wan_project2.data.entity.AppointmentEntity
import com.example.a216839_wan_project2.data.entity.BmiRecordEntity
import com.example.a216839_wan_project2.data.entity.HealthProfileEntity
import kotlinx.coroutines.flow.Flow

// ── HEALTH PROFILE DAO ───────────────────────────────────────────────────────
@Dao
interface HealthProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: HealthProfileEntity)

    @Update
    suspend fun update(profile: HealthProfileEntity)

    @Delete
    suspend fun delete(profile: HealthProfileEntity)

    @Query("SELECT * FROM health_profile LIMIT 1")
    fun getProfile(): Flow<HealthProfileEntity?>

    @Query("SELECT * FROM health_profile LIMIT 1")
    suspend fun getProfileOnce(): HealthProfileEntity?
}

// ── APPOINTMENT DAO ──────────────────────────────────────────────────────────
@Dao
interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appointments: List<AppointmentEntity>)

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Delete
    suspend fun delete(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAll(): Flow<List<AppointmentEntity>>

    @Query("SELECT COUNT(*) FROM appointments WHERE type = 'Upcoming'")
    fun getUpcomingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments WHERE type = 'Completed'")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun countAll(): Int
}

// ── BMI RECORD DAO ───────────────────────────────────────────────────────────
@Dao
interface BmiRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BmiRecordEntity)

    @Delete
    suspend fun delete(record: BmiRecordEntity)

    @Query("SELECT * FROM bmi_records ORDER BY recordedAt DESC")
    fun getAll(): Flow<List<BmiRecordEntity>>

    @Query("SELECT * FROM bmi_records ORDER BY recordedAt DESC LIMIT 1")
    fun getLatest(): Flow<BmiRecordEntity?>

    @Query("DELETE FROM bmi_records")
    suspend fun deleteAll()
}