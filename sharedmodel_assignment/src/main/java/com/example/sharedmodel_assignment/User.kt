package com.example.sharedmodel_assignment

data class User(
    val email: String,
    val name: String,
    val password: String
)

data class UserProfile(
    val email: String,
    val password: String
)