package com.example.a216839_wan_project1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// ── DATA CLASS ────────────────────────────────────────────────────────────────
data class HealthProfile(
    val fullName      : String = "",
    val icNumber      : String = "",
    val dateOfBirth   : String = "",
    val bloodType     : String = "",
    val allergies     : String = "",
    val conditions    : String = "",
    val emergencyName : String = "",
    val emergencyPhone: String = ""
)

// ── VIEW MODEL ────────────────────────────────────────────────────────────────
class HealthProfileViewModel : ViewModel() {

    var profile by mutableStateOf(HealthProfile())
        private set

    fun updateProfile(updated: HealthProfile) {
        profile = updated
    }

    fun updateFullName(value: String)       { profile = profile.copy(fullName       = value) }
    fun updateIcNumber(value: String)       { profile = profile.copy(icNumber       = value) }
    fun updateDateOfBirth(value: String)    { profile = profile.copy(dateOfBirth    = value) }
    fun updateBloodType(value: String)      { profile = profile.copy(bloodType      = value) }
    fun updateAllergies(value: String)      { profile = profile.copy(allergies      = value) }
    fun updateConditions(value: String)     { profile = profile.copy(conditions     = value) }
    fun updateEmergencyName(value: String)  { profile = profile.copy(emergencyName  = value) }
    fun updateEmergencyPhone(value: String) { profile = profile.copy(emergencyPhone = value) }

    fun isProfileComplete(): Boolean =
        profile.fullName.isNotBlank() && profile.icNumber.isNotBlank()
}