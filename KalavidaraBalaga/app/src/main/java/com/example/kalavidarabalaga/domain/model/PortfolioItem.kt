package com.example.kalavidarabalaga.domain.model

data class PortfolioItem(
    val id: String = "",
    val troupeId: String = "",
    val photoUrl: String = "",
    val caption: String = "",
    val uploadedAt: Long = 0L
)
