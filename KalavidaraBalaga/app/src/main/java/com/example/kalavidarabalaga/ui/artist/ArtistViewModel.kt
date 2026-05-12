package com.example.kalavidarabalaga.ui.artist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.graphics.Bitmap
import com.example.kalavidarabalaga.data.repository.AuthRepository
import com.example.kalavidarabalaga.data.repository.GeminiRepository
import com.example.kalavidarabalaga.data.repository.TroupeRepository
import com.example.kalavidarabalaga.domain.model.PortfolioItem
import com.example.kalavidarabalaga.domain.model.Troupe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val troupeRepository: TroupeRepository,
    private val authRepository: AuthRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _troupe = MutableStateFlow(Troupe())
    val troupe: StateFlow<Troupe> = _troupe.asStateFlow()

    private val _portfolio = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val portfolio: StateFlow<List<PortfolioItem>> = _portfolio.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadTroupeData()
    }

    private fun loadTroupeData() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val existingTroupe = troupeRepository.getTroupeProfile(uid)
            if (existingTroupe != null) {
                _troupe.value = existingTroupe
                loadPortfolio(uid)
            } else {
                _troupe.value = Troupe(troupeId = uid)
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadPortfolio(troupeId: String) {
        _portfolio.value = troupeRepository.getPortfolio(troupeId)
    }

    fun updateTroupeInfo(troupe: Troupe) {
        _troupe.value = troupe
    }

    fun saveTroupeProfile(photoUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            var photoUrl = _troupe.value.photoUrl
            if (photoUri != null) {
                val uploadedUrl = troupeRepository.uploadPhoto(photoUri, "troupes")
                if (uploadedUrl != null) photoUrl = uploadedUrl
            }
            val finalTroupe = _troupe.value.copy(photoUrl = photoUrl)
            val success = troupeRepository.saveTroupeProfile(finalTroupe)
            if (success) {
                _troupe.value = finalTroupe
                _errorMessage.value = "Profile Saved Successfully!"
            } else {
                _errorMessage.value = "Failed to save profile. Check Firestore rules or internet."
            }
            _isLoading.value = false
        }
    }

    fun uploadPortfolioPhoto(uri: Uri, caption: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val photoUrl = troupeRepository.uploadPhoto(uri, "portfolio")
            if (photoUrl != null) {
                val item = PortfolioItem(
                    troupeId = uid,
                    photoUrl = photoUrl,
                    caption = caption,
                    uploadedAt = System.currentTimeMillis()
                )
                if (troupeRepository.addPortfolioItem(item)) {
                    loadPortfolio(uid)
                }
            }
            _isLoading.value = false
        }
    }
    fun generateBio() {
        viewModelScope.launch {
            _isLoading.value = true
            val artForm = _troupe.value.artForm.ifEmpty { "Traditional Arts" }
            val districts = _troupe.value.districts.joinToString(", ").ifEmpty { "Karnataka" }
            val name = _troupe.value.name.ifEmpty { "This troupe" }
            
            val experience = 5 // Defaulting for simplicity
            val members = 10 // Defaulting for simplicity
            val speciality = "Performing authentic $artForm across $districts"
            
            val generatedBio = geminiRepository.generateBio(artForm, experience, members, speciality)
            if (generatedBio != null) {
                _troupe.value = _troupe.value.copy(bio = generatedBio)
                _errorMessage.value = "Bio Generated Successfully!"
            } else {
                _errorMessage.value = "Failed to generate bio."
            }
            _isLoading.value = false
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun uploadPortfolioPhotoWithAI(uri: Uri, bitmap: Bitmap) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            
            // 1. Generate Caption with Vision API
            val generatedCaption = geminiRepository.generateCaptionForImage(bitmap) ?: "Traditional Performance"
            
            // 2. Bypass Firebase Storage: Compress and store as Base64 in Firestore
            try {
                val scaledBitmap = scaleBitmap(bitmap, 600)
                val outputStream = java.io.ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                val base64String = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
                val photoUrl = "data:image/jpeg;base64,$base64String"
                
                // 3. Save to Firestore
                val item = PortfolioItem(
                    troupeId = uid,
                    photoUrl = photoUrl,
                    caption = generatedCaption,
                    uploadedAt = System.currentTimeMillis()
                )
                if (troupeRepository.addPortfolioItem(item)) {
                    loadPortfolio(uid)
                    _errorMessage.value = "Photo uploaded to Portfolio!"
                } else {
                    _errorMessage.value = "Failed to save portfolio item to Firestore."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Image processing failed."
            }
            
            _isLoading.value = false
        }
    }
}
