package com.example.myproject.model

data class RequestModel(
    val requestId: String = "",
    val skillWanted: String = "",
    val description: String = "",
    val budget: String = "",
    val contactInfo: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "requestId" to requestId,
            "skillWanted" to skillWanted,
            "description" to description,
            "budget" to budget,
            "contactInfo" to contactInfo
        )
    }
}