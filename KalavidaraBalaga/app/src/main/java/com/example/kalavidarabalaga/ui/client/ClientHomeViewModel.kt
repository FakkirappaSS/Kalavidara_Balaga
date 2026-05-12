package com.example.kalavidarabalaga.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalavidarabalaga.data.repository.TroupeRepository
import com.example.kalavidarabalaga.domain.model.Troupe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientHomeViewModel @Inject constructor(
    private val troupeRepository: TroupeRepository
) : ViewModel() {

    private val _troupes = MutableStateFlow<List<Troupe>>(emptyList())
    val troupes: StateFlow<List<Troupe>> = _troupes.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchDistrict = MutableStateFlow("")
    val searchDistrict: StateFlow<String> = _searchDistrict.asStateFlow()

    private val _searchArtForm = MutableStateFlow("")
    val searchArtForm: StateFlow<String> = _searchArtForm.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Initial load of all approved troupes
        searchTroupes()
    }

    fun updateDistrictFilter(district: String) {
        _searchDistrict.value = district
        debouncedSearch()
    }

    fun updateArtFormFilter(artForm: String) {
        _searchArtForm.value = artForm
        debouncedSearch()
    }

    private fun debouncedSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            delay(500) // Debounce typing
            searchTroupes()
        }
    }

    private fun searchTroupes() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Fetch all approved troupes first
            val allApprovedTroupes = troupeRepository.searchTroupes(null, null)
            
            // Perform powerful in-memory filtering to avoid strict Firestore case sensitivity
            val districtQuery = _searchDistrict.value.trim().lowercase()
            val artFormQuery = _searchArtForm.value.trim().lowercase()
            
            _troupes.value = allApprovedTroupes.filter { troupe ->
                val matchesDistrict = if (districtQuery.isEmpty()) true else {
                    troupe.districts.any { it.lowercase().contains(districtQuery) }
                }
                
                val matchesArtForm = if (artFormQuery.isEmpty()) true else {
                    troupe.artForm.lowercase().contains(artFormQuery)
                }
                
                matchesDistrict && matchesArtForm
            }
            _isLoading.value = false
        }
    }
    fun incrementInquiryCount(troupeId: String) {
        viewModelScope.launch {
            troupeRepository.incrementCounter(troupeId, "inquiryCount")
        }
    }

    fun incrementViewCount(troupeId: String) {
        viewModelScope.launch {
            troupeRepository.incrementCounter(troupeId, "viewCount")
        }
    }
    
    private val _selectedPortfolio = MutableStateFlow<List<com.example.kalavidarabalaga.domain.model.PortfolioItem>>(emptyList())
    val selectedPortfolio: StateFlow<List<com.example.kalavidarabalaga.domain.model.PortfolioItem>> = _selectedPortfolio.asStateFlow()
    
    fun loadTroupePortfolio(troupeId: String) {
        viewModelScope.launch {
            _selectedPortfolio.value = troupeRepository.getPortfolio(troupeId)
        }
    }
}
