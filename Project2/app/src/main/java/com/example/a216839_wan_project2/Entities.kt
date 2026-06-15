package com.example.a216839_wan_project2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── HEALTH PROFILE ENTITY ────────────────────────────────────────────────────
@Entity(tableName = "health_profile")
data class HealthProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id            : Int    = 0,
    val fullName      : String = "",
    val icNumber      : String = "",
    val dateOfBirth   : String = "",
    val bloodType     : String = "",
    val allergies     : String = "",
    val conditions    : String = "",
    val emergencyName : String = "",
    val emergencyPhone: String = ""
)

// ── APPOINTMENT ENTITY ───────────────────────────────────────────────────────
@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id      : Int    = 0,
    val title   : String = "",
    val facility: String = "",
    val date    : String = "",
    val time    : String = "",
    val type    : String = ""   // "Upcoming" or "Completed"
)

// ── BMI RECORD ENTITY ────────────────────────────────────────────────────────
@Entity(tableName = "bmi_records")
data class BmiRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id        : Int    = 0,
    val weightKg  : Double = 0.0,
    val heightCm  : Double = 0.0,
    val bmi       : Double = 0.0,
    val category  : String = "",
    val recordedAt: Long   = System.currentTimeMillis()
)

