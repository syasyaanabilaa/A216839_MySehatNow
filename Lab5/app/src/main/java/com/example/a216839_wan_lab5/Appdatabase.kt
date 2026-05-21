package com.example.a216839_wan_lab5.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.a216839_wan_lab5.data.dao.AppointmentDao
import com.example.a216839_wan_lab5.data.dao.BmiRecordDao
import com.example.a216839_wan_lab5.data.dao.HealthProfileDao
import com.example.a216839_wan_lab5.data.entity.AppointmentEntity
import com.example.a216839_wan_lab5.data.entity.BmiRecordEntity
import com.example.a216839_wan_lab5.data.entity.HealthProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities  = [HealthProfileEntity::class, AppointmentEntity::class, BmiRecordEntity::class],
    version   = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun healthProfileDao(): HealthProfileDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun bmiRecordDao(): BmiRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mysehatnow_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate appointments on first launch
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.appointmentDao().insertAll(defaultAppointments())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun defaultAppointments() = listOf(
            AppointmentEntity(title = "Blood Pressure Check", facility = "KK Cheras",       date = "25 Jul 2025", time = "9:00 AM",  type = "Upcoming"),
            AppointmentEntity(title = "General Consultation", facility = "KK Kajang",        date = "20 Jul 2025", time = "10:30 AM", type = "Upcoming"),
            AppointmentEntity(title = "Diabetes Follow-Up",   facility = "Hospital Kajang",  date = "10 Jul 2025", time = "2:15 PM",  type = "Completed"),
            AppointmentEntity(title = "Eye Screening",        facility = "KK Bangi",         date = "5 Jun 2025",  time = "8:45 AM",  type = "Completed"),
            AppointmentEntity(title = "Dental Check",         facility = "KK Sg Chua",       date = "20 Apr 2025", time = "11:00 AM", type = "Completed")
        )
    }
}