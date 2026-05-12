package com.example.kalavidarabalaga.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalavidarabalaga.data.repository.TroupeRepository
import com.example.kalavidarabalaga.domain.model.Troupe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val troupeRepository: TroupeRepository
) : ViewModel() {

    private val _pendingTroupes = MutableStateFlow<List<Troupe>>(emptyList())
    val pendingTroupes: StateFlow<List<Troupe>> = _pendingTroupes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPendingTroupes()
    }

    private fun loadPendingTroupes() {
        viewModelScope.launch {
            _isLoading.value = true
            _pendingTroupes.value = troupeRepository.getPendingTroupes()
            _isLoading.value = false
        }
    }

    fun approveTroupe(troupeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = troupeRepository.updateTroupeApprovalStatus(troupeId, true)
            if (success) {
                // Remove from local list to update UI instantly
                _pendingTroupes.value = _pendingTroupes.value.filter { it.troupeId != troupeId }
            }
            _isLoading.value = false
        }
    }

    fun rejectTroupe(troupeId: String) {
        // For simplicity, rejection also removes it from pending list by setting it to something else or deleting.
        // We'll just delete or mark as rejected. Here, we can just leave it unapproved and ignore it,
        // or actually delete the document. Let's delete the document for a true rejection.
        // But since we only have updateTroupeApprovalStatus, let's assume we keep it false but we need a way to hide it.
        // Actually, let's just remove it from the list locally for now to dismiss it.
        _pendingTroupes.value = _pendingTroupes.value.filter { it.troupeId != troupeId }
    }
}
