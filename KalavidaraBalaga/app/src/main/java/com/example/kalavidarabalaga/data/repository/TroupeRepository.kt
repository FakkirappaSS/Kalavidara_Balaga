package com.example.kalavidarabalaga.data.repository

import android.net.Uri
import com.example.kalavidarabalaga.domain.model.PortfolioItem
import com.example.kalavidarabalaga.domain.model.Troupe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TroupeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    suspend fun saveTroupeProfile(troupe: Troupe): Boolean {
        return try {
            firestore.collection("troupes").document(troupe.troupeId).set(troupe).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("TroupeRepo", "saveTroupeProfile error", e)
            false
        }
    }

    suspend fun getTroupeProfile(troupeId: String): Troupe? {
        return try {
            val snapshot = firestore.collection("troupes").document(troupeId).get().await()
            snapshot.toObject(Troupe::class.java)
        } catch (e: Exception) {
            android.util.Log.e("TroupeRepo", "getTroupeProfile error", e)
            null
        }
    }

    suspend fun uploadPhoto(uri: Uri, path: String): String? {
        return try {
            val fileName = UUID.randomUUID().toString()
            val ref = storage.reference.child("$path/$fileName")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addPortfolioItem(item: PortfolioItem): Boolean {
        return try {
            val docRef = firestore.collection("portfolio").document()
            val newItem = item.copy(id = docRef.id)
            docRef.set(newItem).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getPortfolio(troupeId: String): List<PortfolioItem> {
        return try {
            val snapshot = firestore.collection("portfolio")
                .whereEqualTo("troupeId", troupeId)
                .get()
                .await()
            snapshot.toObjects(PortfolioItem::class.java).sortedByDescending { it.uploadedAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchTroupes(district: String?, artForm: String?): List<Troupe> {
        return try {
            var query: com.google.firebase.firestore.Query = firestore.collection("troupes")
                .whereEqualTo("approved", true) // Ensure we only show approved troupes

            if (!district.isNullOrEmpty()) {
                query = query.whereArrayContains("districts", district)
            }
            if (!artForm.isNullOrEmpty()) {
                query = query.whereEqualTo("artForm", artForm)
            }

            val snapshot = query.get().await()
            snapshot.toObjects(Troupe::class.java)
        } catch (e: Exception) {
            android.util.Log.e("TroupeRepo", "searchTroupes error", e)
            emptyList()
        }
    }

    suspend fun getPendingTroupes(): List<Troupe> {
        return try {
            val snapshot = firestore.collection("troupes")
                .whereEqualTo("approved", false)
                .get().await()
            snapshot.toObjects(Troupe::class.java)
        } catch (e: Exception) {
            android.util.Log.e("TroupeRepo", "getPendingTroupes error", e)
            emptyList()
        }
    }

    suspend fun updateTroupeApprovalStatus(troupeId: String, isApproved: Boolean): Boolean {
        return try {
            firestore.collection("troupes").document(troupeId)
                .update("approved", isApproved).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun incrementCounter(troupeId: String, field: String) {
        try {
            firestore.collection("troupes").document(troupeId)
                .update(field, com.google.firebase.firestore.FieldValue.increment(1)).await()
        } catch (e: Exception) {
            android.util.Log.e("TroupeRepo", "incrementCounter error", e)
        }
    }
}
