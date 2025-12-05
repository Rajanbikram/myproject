package com.example.myproject.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "email" to email
        )
    }
}