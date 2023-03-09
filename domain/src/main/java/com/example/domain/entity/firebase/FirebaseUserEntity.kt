package com.example.domain.entity.firebase

data class FirebaseUserEntity(
    var userId: String? = null,
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val about: String? = null
)