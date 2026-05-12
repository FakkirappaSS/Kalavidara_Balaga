package com.example.kalavidarabalaga.domain.model

data class Booking(
    val bookingId: String = "",
    val clientId: String = "",
    val troupeId: String = "",
    val eventDate: Long = 0L,
    val eventType: String = "",
    val message: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BookingStatus {
    PENDING, CONFIRMED, REJECTED
}
