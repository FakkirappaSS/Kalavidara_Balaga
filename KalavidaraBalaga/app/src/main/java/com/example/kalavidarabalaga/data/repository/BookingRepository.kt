package com.example.kalavidarabalaga.data.repository

import com.example.kalavidarabalaga.domain.model.Booking
import com.example.kalavidarabalaga.domain.model.BookingStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createBooking(booking: Booking): Boolean {
        return try {
            val docRef = firestore.collection("bookings").document()
            val newBooking = booking.copy(bookingId = docRef.id)
            docRef.set(newBooking).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("BookingRepo", "createBooking error", e)
            false
        }
    }

    suspend fun getTroupeBookings(troupeId: String): List<Booking> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("troupeId", troupeId)
                .get().await()
            snapshot.toObjects(Booking::class.java).sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            android.util.Log.e("BookingRepo", "getTroupeBookings error", e)
            emptyList()
        }
    }

    suspend fun getClientBookings(clientId: String): List<Booking> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("clientId", clientId)
                .get().await()
            snapshot.toObjects(Booking::class.java).sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            android.util.Log.e("BookingRepo", "getClientBookings error", e)
            emptyList()
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Boolean {
        return try {
            firestore.collection("bookings").document(bookingId)
                .update("status", status.name).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("BookingRepo", "updateBookingStatus error", e)
            false
        }
    }
}
