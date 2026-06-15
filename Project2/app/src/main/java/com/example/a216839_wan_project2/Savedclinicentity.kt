package com.example.a216839_wan_project2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── Room entity — one saved clinic ───────────────────────────────────────────
// When user taps "Save" on a clinic card, it gets stored here permanently
@Entity(tableName = "saved_clinics")
data class SavedClinicEntity(
    @PrimaryKey(autoGenerate = true)
    val id        : Int    = 0,
    val name      : String = "",
    val address   : String = "",
    val phone     : String = "",
    val distanceKm: Double = 0.0,
    val savedAt   : Long   = System.currentTimeMillis()
)
