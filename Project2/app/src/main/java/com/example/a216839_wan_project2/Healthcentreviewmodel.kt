package com.example.a216839_wan_project2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a216839_wan_project2.data.api.OverpassApi
import com.example.a216839_wan_project2.data.api.OverpassElement
import com.example.a216839_wan_project2.data.entity.SavedClinicEntity
import com.example.a216839_wan_project2.data.firebase.FirestoreClinicRepository
import com.example.a216839_wan_project2.data.firebase.SharedClinicReport
import com.example.a216839_wan_project2.data.repository.SavedClinicRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ── UI STATES ─────────────────────────────────────────────────────────────────
sealed class HealthCentreUiState {
    object Idle       : HealthCentreUiState()
    object LoadingGps : HealthCentreUiState()
    object LoadingApi : HealthCentreUiState()
    data class Success(
        val clinics : List<OverpassElement>,
        val userLat : Double,
        val userLon : Double
    ) : HealthCentreUiState()
    data class Error(val message: String) : HealthCentreUiState()
}

class HealthCentreViewModel(
    app                  : Application,
    private val savedRepo: SavedClinicRepository   // ← Room repo injected
) : AndroidViewModel(app) {

    private val fusedLocation =
        LocationServices.getFusedLocationProviderClient(app)

    // ── Clinic search state ───────────────────────────────────────────────────
    private val _uiState = MutableStateFlow<HealthCentreUiState>(HealthCentreUiState.Idle)
    val uiState: StateFlow<HealthCentreUiState> = _uiState.asStateFlow()

    // ── Toast/snackbar message ────────────────────────────────────────────────
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // ── Room: saved clinics list (reactive) ───────────────────────────────────
    val savedClinics = savedRepo.allSaved
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Firebase: shared clinic reports (reactive) ────────────────────────────
    val sharedReports = FirestoreClinicRepository.getSharedClinicsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── PILLAR 1 + 2: GPS Sensor → Overpass Web API ───────────────────────────
    @SuppressLint("MissingPermission")
    fun findNearbyClinics() {
        viewModelScope.launch {

            // Step 1 — GPS SENSOR reads location
            _uiState.value = HealthCentreUiState.LoadingGps
            val location: Location? = try {
                val cts = CancellationTokenSource()
                fusedLocation.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, cts.token
                ).await()
            } catch (e: Exception) {
                _uiState.value = HealthCentreUiState.Error(
                    "Could not get location.\nPlease enable GPS and try again.\n(${e.message})"
                )
                return@launch
            }

            if (location == null) {
                _uiState.value = HealthCentreUiState.Error(
                    "Location unavailable.\nPlease enable GPS and try again."
                )
                return@launch
            }

            val lat = location.latitude
            val lon = location.longitude

            // Malaysia bounds check
            if (!OverpassApi.isInMalaysia(lat, lon)) {
                _uiState.value = HealthCentreUiState.Error(
                    "⚠️ Your location appears to be outside Malaysia.\n\n" +
                            "This app only shows health centres in Malaysia.\n\n" +
                            "Please ensure your GPS is working correctly."
                )
                return@launch
            }

            // Step 2 — WEB API: send coordinates to Overpass API
            _uiState.value = HealthCentreUiState.LoadingApi
            try {
                val query    = OverpassApi.buildClinicQuery(lat, lon, radiusMetres = 5000)
                val response = OverpassApi.fetchClinics(query)
                val clinics  = response.elements

                _uiState.value = if (clinics.isEmpty()) {
                    HealthCentreUiState.Error(
                        "No health centres found within 5km.\nTry again in a different area."
                    )
                } else {
                    HealthCentreUiState.Success(clinics, lat, lon)
                }
            } catch (e: Exception) {
                _uiState.value = HealthCentreUiState.Error(
                    "Failed to load clinics.\n${e.message}\nCheck your internet connection."
                )
            }
        }
    }

    // ── PILLAR 3: Save clinic to Room (LOCAL PERSISTENCE) ────────────────────
    fun saveClinic(clinic: OverpassElement, distanceKm: Double) {
        viewModelScope.launch {
            val alreadySaved = savedRepo.isSaved(clinic.name)
            if (alreadySaved) {
                _message.value = "Already saved!"
                return@launch
            }
            savedRepo.insert(
                SavedClinicEntity(
                    name       = clinic.name,
                    address    = clinic.address,
                    phone      = clinic.phone,
                    distanceKm = distanceKm
                )
            )
            _message.value = "✅ ${clinic.name} saved to your list!"
        }
    }

    fun deleteSaved(clinic: SavedClinicEntity) {
        viewModelScope.launch {
            savedRepo.delete(clinic)
            _message.value = "Removed from saved list."
        }
    }

    // ── PILLAR 4: Share clinic to Firebase (CLOUD) ────────────────────────────
    fun shareClinicToFirebase(
        clinic    : OverpassElement,
        distanceKm: Double,
        reportedBy: String,
        note      : String
    ) {
        viewModelScope.launch {
            val report = SharedClinicReport(
                clinicName  = clinic.name,
                address     = clinic.address,
                phone       = clinic.phone,
                distanceKm  = distanceKm,
                reportedBy  = reportedBy.ifBlank { "Anonymous" },
                note        = note.ifBlank { "Recommended by community member" },
                timestamp   = System.currentTimeMillis()
            )
            val result = FirestoreClinicRepository.shareClinic(report)
            _message.value = if (result.isSuccess)
                "🌐 Shared to community board!"
            else
                "Failed to share. Check internet."
        }
    }

    fun deleteSharedReport(id: String) {
        viewModelScope.launch {
            FirestoreClinicRepository.deleteReport(id)
        }
    }

    fun reset() { _uiState.value = HealthCentreUiState.Idle }
    fun clearMessage() { _message.value = null }

    fun distanceKm(userLat: Double, userLon: Double, clinicLat: Double, clinicLon: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(userLat, userLon, clinicLat, clinicLon, results)
        return (results[0] / 1000.0 * 10).toInt() / 10.0
    }
}
