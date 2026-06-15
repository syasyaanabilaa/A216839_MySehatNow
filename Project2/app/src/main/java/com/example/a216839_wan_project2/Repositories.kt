package com.example.a216839_wan_project2.data.repository

import com.example.a216839_wan_project2.data.dao.AppointmentDao
import com.example.a216839_wan_project2.data.dao.BmiRecordDao
import com.example.a216839_wan_project2.data.dao.HealthProfileDao
import com.example.a216839_wan_project2.data.entity.AppointmentEntity
import com.example.a216839_wan_project2.data.entity.BmiRecordEntity
import com.example.a216839_wan_project2.data.entity.HealthProfileEntity
import kotlinx.coroutines.flow.Flow

// ── HEALTH PROFILE REPOSITORY ────────────────────────────────────────────────
class HealthProfileRepository(private val dao: HealthProfileDao) {

    val profile: Flow<HealthProfileEntity?> = dao.getProfile()

    suspend fun saveProfile(entity: HealthProfileEntity) {
        val existing = dao.getProfileOnce()
        if (existing == null) {
            dao.insert(entity)
        } else {
            dao.update(entity.copy(id = existing.id))
        }
    }

    suspend fun deleteProfile(entity: HealthProfileEntity) = dao.delete(entity)
}

// ── APPOINTMENT REPOSITORY ───────────────────────────────────────────────────
class AppointmentRepository(private val dao: AppointmentDao) {

    val allAppointments: Flow<List<AppointmentEntity>> = dao.getAll()
    val upcomingCount  : Flow<Int>                     = dao.getUpcomingCount()
    val completedCount : Flow<Int>                     = dao.getCompletedCount()

    suspend fun insert(appointment: AppointmentEntity) = dao.insert(appointment)
    suspend fun update(appointment: AppointmentEntity) = dao.update(appointment)
    suspend fun delete(appointment: AppointmentEntity) = dao.delete(appointment)
    suspend fun countAll(): Int                        = dao.countAll()
    suspend fun insertAll(list: List<AppointmentEntity>) = dao.insertAll(list)
}

// ── BMI REPOSITORY ────────────────────────────────────────────────────────────
class BmiRepository(private val dao: BmiRecordDao) {

    val allRecords : Flow<List<BmiRecordEntity>> = dao.getAll()
    val latestRecord: Flow<BmiRecordEntity?>    = dao.getLatest()

    suspend fun insert(record: BmiRecordEntity)  = dao.insert(record)
    suspend fun delete(record: BmiRecordEntity)  = dao.delete(record)
    suspend fun deleteAll()                      = dao.deleteAll()
}