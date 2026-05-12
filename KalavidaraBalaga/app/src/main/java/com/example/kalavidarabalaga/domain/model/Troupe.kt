package com.example.kalavidarabalaga.domain.model

data class Troupe(
    val troupeId: String = "",
    val name: String = "",
    val artForm: String = "",
    val districts: List<String> = emptyList(),
    val contactPhone: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val equipmentList: List<String> = emptyList(),
    val approved: Boolean = false,
    val inquiryCount: Int = 0,
    val viewCount: Int = 0,
    val videoLinks: List<String> = emptyList()
)
