package com.example.kalavidarabalaga.domain.model

enum class Role { ARTIST, CLIENT, ADMIN }

data class User(
    val uid: String = "",
    val role: Role = Role.CLIENT,
    val troupeId: String? = null
)
