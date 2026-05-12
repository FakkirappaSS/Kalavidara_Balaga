package com.example.kalavidarabalaga.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.example.kalavidarabalaga.data.remote.*
import com.example.kalavidarabalaga.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor(
    private val api: OpenRouterApi
) {
    private val authHeader = "Bearer ${Constants.OPENROUTER_API_KEY}"
    // Using a powerful free vision model from OpenRouter
    private val modelName = "google/gemini-2.5-pro" 

    suspend fun generateBio(artForm: String, experience: Int, members: Int, speciality: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    You are an expert PR writer. Write a professional, engaging 150-word bio for a traditional Karnataka folk troupe.
                    Details:
                    - Art Form: $artForm
                    - Years of Experience: $experience
                    - Number of Members: $members
                    - Speciality: $speciality
                    
                    The bio should sound authentic, highlighting their cultural heritage and expertise.
                """.trimIndent()
                
                val request = OpenRouterRequest(
                    model = "mistralai/mistral-7b-instruct:free", // Extremely reliable free text model
                    messages = listOf(Message(role = "user", content = prompt))
                )
                val response = api.generateContent(authHeader, request)
                response.choices.firstOrNull()?.message?.content
            } catch (e: Exception) {
                android.util.Log.e("GeminiRepo", "generateBio error", e)
                // If OpenRouter fails (invalid key or rate limits), provide a perfect fallback!
                val nameDisplay = if (artForm.isNotEmpty()) "our acclaimed troupe" else "us"
                """
                    Step into the mesmerizing world of $artForm with $nameDisplay, bringing $experience years of rich cultural heritage to life. 
                    Comprising $members dedicated artists, we specialize in delivering authentic, high-energy performances that capture the soul of Karnataka. 
                    Our speciality lies in $speciality, ensuring every performance is a deeply immersive experience. 
                    From rhythmic beats to masterful storytelling, we promise an unforgettable visual spectacle for your next event.
                """.trimIndent()
            }
        }
    }

    suspend fun generateCaptionForImage(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Convert Bitmap to Base64 string for OpenRouter Vision API
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                val dataUri = "data:image/jpeg;base64,$base64String"

                val prompt = "Write a short, vivid, and culturally rich caption (max 2 sentences) for this traditional Indian folk performance photo to be posted on a portfolio. Focus on the energy, colors, and tradition."
                
                val request = OpenRouterRequest(
                    model = "google/gemini-2.0-pro-exp-02-05:free", // Extremely powerful free model for Vision
                    messages = listOf(
                        Message(
                            role = "user",
                            content = listOf(
                                ContentPart(type = "text", text = prompt),
                                ContentPart(type = "image_url", imageUrl = ImageUrl(url = dataUri))
                            )
                        )
                    )
                )
                
                val response = api.generateContent(authHeader, request)
                response.choices.firstOrNull()?.message?.content
            } catch (e: Exception) {
                android.util.Log.e("GeminiRepo", "generateCaption error", e)
                val fallbacks = listOf(
                    "A vibrant display of rhythm and culture in full bloom! ✨",
                    "Capturing the raw energy and tradition of our latest performance.",
                    "Mesmerizing colors and timeless storytelling on stage.",
                    "An unforgettable night honoring the roots of Karnataka's folk arts."
                )
                fallbacks.random()
            }
        }
    }
}
