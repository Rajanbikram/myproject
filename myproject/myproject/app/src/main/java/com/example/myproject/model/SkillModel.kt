package com.example.myproject.model

data class SkillModel(
    val skillId: String = "",
    val skillTitle: String = "",
    val description: String = "",
    val price: String = "",
    val contactInfo: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "skillId" to skillId,
            "skillTitle" to skillTitle,
            "description" to description,
            "price" to price,
            "contactInfo" to contactInfo
        )
    }
}