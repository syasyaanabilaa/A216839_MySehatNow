package com.example.a216839_wan_project2.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class SavedTip(
    @get:Exclude @set:Exclude
    var id       : String = "",
    val tip      : String = "",
    val category : String = "",
    val query    : String = "",
    val savedAt  : Long   = System.currentTimeMillis()
)

object FirestoreTipsRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // saved_tips is now a root collection — same level as shared_clinics
    private fun savedCol() = db.collection("saved_tips")

    // ── Saved tips flow ───────────────────────────────────────────────────────
    fun getSavedTipsFlow(): Flow<List<SavedTip>> = callbackFlow {
        val ref = savedCol()
            .orderBy("savedAt", Query.Direction.DESCENDING)
            .limit(50)

        val listener = ref.addSnapshotListener { snap: QuerySnapshot?, err: Exception? ->
            if (err != null) {
                Log.e("FirestoreRepo", "Listen failed: ${err.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { d: DocumentSnapshot ->
                d.toObject(SavedTip::class.java)?.also { it.id = d.id }
            } ?: emptyList()
            Log.d("FirestoreRepo", "Loaded ${list.size} saved tips")
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    // ── Save a tip ────────────────────────────────────────────────────────────
    suspend fun saveTip(tip: String, category: String, query: String): Result<Unit> {
        return try {
            val entry = SavedTip(
                tip      = tip,
                category = category,
                query    = query,
                savedAt  = System.currentTimeMillis()
            )
            val docRef = savedCol().add(entry).await()
            Log.d("FirestoreRepo", "Tip saved! Doc ID: ${docRef.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to save tip: ${e.message}")
            Result.failure(e)
        }
    }

    // ── Delete a saved tip ────────────────────────────────────────────────────
    suspend fun deleteSavedTip(tipId: String): Result<Unit> {
        return try {
            savedCol().document(tipId).delete().await()
            Log.d("FirestoreRepo", "Tip deleted: $tipId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to delete tip: ${e.message}")
            Result.failure(e)
        }
    }
}
