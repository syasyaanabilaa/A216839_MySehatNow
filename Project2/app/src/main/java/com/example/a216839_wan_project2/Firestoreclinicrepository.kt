package com.example.a216839_wan_project2.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// ── DATA MODEL ────────────────────────────────────────────────────────────────
// When a user shares a clinic to Firebase, this is what gets stored
data class SharedClinicReport(
    val id          : String = "",
    val clinicName  : String = "",
    val address     : String = "",
    val phone       : String = "",
    val distanceKm  : Double = 0.0,
    val reportedBy  : String = "",   // user's name or "Anonymous"
    val note        : String = "",   // e.g. "Good service, short wait time"
    val timestamp   : Long   = System.currentTimeMillis()
)

// ── FIRESTORE REPOSITORY ──────────────────────────────────────────────────────
object FirestoreClinicRepository {

    private val db         = FirebaseFirestore.getInstance()
    private val collection = db.collection("shared_clinics")

    // Listen to shared clinic reports in real-time
    fun getSharedClinicsFlow(): Flow<List<SharedClinicReport>> = callbackFlow {
        val listener = collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(SharedClinicReport::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // Share a clinic report to Firebase
    suspend fun shareClinic(report: SharedClinicReport): Result<Unit> {
        return try {
            collection.add(report).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete a report
    suspend fun deleteReport(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
