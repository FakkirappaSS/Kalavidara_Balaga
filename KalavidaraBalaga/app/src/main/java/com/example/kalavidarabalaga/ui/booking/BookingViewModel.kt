package com.example.kalavidarabalaga.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalavidarabalaga.data.repository.AuthRepository
import com.example.kalavidarabalaga.data.repository.BookingRepository
import com.example.kalavidarabalaga.domain.model.Booking
import com.example.kalavidarabalaga.domain.model.BookingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _artistBookings = MutableStateFlow<List<Booking>>(emptyList())
    val artistBookings: StateFlow<List<Booking>> = _artistBookings.asStateFlow()

    private val _clientBookings = MutableStateFlow<List<Booking>>(emptyList())
    val clientBookings: StateFlow<List<Booking>> = _clientBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadArtistBookings() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _artistBookings.value = bookingRepository.getTroupeBookings(uid)
            _isLoading.value = false
        }
    }

    fun loadClientBookings() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _clientBookings.value = bookingRepository.getClientBookings(uid)
            _isLoading.value = false
        }
    }

    fun createBooking(troupeId: String, eventDate: Long, eventType: String, message: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val booking = Booking(
                clientId = uid,
                troupeId = troupeId,
                eventDate = eventDate,
                eventType = eventType,
                message = message
            )
            if (bookingRepository.createBooking(booking)) {
                loadClientBookings()
            }
            _isLoading.value = false
        }
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            if (bookingRepository.updateBookingStatus(bookingId, status)) {
                loadArtistBookings()
            }
            _isLoading.value = false
        }
    }
}
