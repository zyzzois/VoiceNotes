package com.example.domain.entity

data class UserModel(
    var userId: String? = null,
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val about: String? = null
)