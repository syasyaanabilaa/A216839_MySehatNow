package com.example.a216839_wan_lab4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// ── DATA CLASS ────────────────────────────────────────────────────────────────
// Fulfils Task 2: Data Class with at least 2 fields
data class HealthProfile(
    val fullName     : String = "",
    val icNumber     : String = "",
    val dateOfBirth  : String = "",
    val bloodType    : String = "",
    val allergies    : String = "",
    val conditions   : String = "",
    val emergencyName: String = "",
    val emergencyPhone: String = ""
)

// ── VIEW MODEL ────────────────────────────────────────────────────────────────
// Fulfils Task 2: ViewModel that holds an instance of the data class,
// survives configuration changes (e.g., screen rotation).
class HealthProfileViewModel : ViewModel() {

    // The single source of truth for the health profile
    var profile by mutableStateOf(HealthProfile())
        private set

    // Called from the form screen; updates the entire profile at once
    fun updateProfile(updated: HealthProfile) {
        profile = updated
    }

    // Convenience helpers so individual fields can be updated reactively
    fun updateFullName(value: String)      { profile = profile.copy(fullName      = value) }
    fun updateIcNumber(value: String)      { profile = profile.copy(icNumber      = value) }
    fun updateDateOfBirth(value: String)   { profile = profile.copy(dateOfBirth   = value) }
    fun updateBloodType(value: String)     { profile = profile.copy(bloodType     = value) }
    fun updateAllergies(value: String)     { profile = profile.copy(allergies     = value) }
    fun updateConditions(value: String)    { profile = profile.copy(conditions    = value) }
    fun updateEmergencyName(value: String) { profile = profile.copy(emergencyName = value) }
    fun updateEmergencyPhone(value: String){ profile = profile.copy(emergencyPhone= value) }

    // Checks that mandatory fields are filled before allowing navigation
    fun isProfileComplete(): Boolean =
        profile.fullName.isNotBlank() && profile.icNumber.isNotBlank()
}