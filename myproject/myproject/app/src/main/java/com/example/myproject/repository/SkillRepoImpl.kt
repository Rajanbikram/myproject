package com.example.myproject.repository

import android.util.Log
import com.example.myproject.model.SkillModel
import com.google.firebase.firestore.FirebaseFirestore

class SkillRepoImpl : SkillRepo {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Helper to get the skills collection reference
    private fun skillsRef(userId: String) =
        db.collection("users").document(userId).collection("skills")

    override fun addSkill(
        userId: String,
        model: SkillModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Auto-generate a document ID
        val docRef = skillsRef(userId).document()
        val skillWithId = model.copy(skillId = docRef.id)

        docRef.set(skillWithId.toMap())
            .addOnSuccessListener {
                callback(true, "Skill added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SkillRepoImpl", "addSkill error", e)
                callback(false, e.localizedMessage ?: "Failed to add skill")
            }
    }

    override fun getSkills(
        userId: String,
        callback: (Boolean, String, List<SkillModel>?) -> Unit
    ) {
        skillsRef(userId).get()
            .addOnSuccessListener { snapshot ->
                val skills = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(SkillModel::class.java)
                }
                callback(true, "Skills fetched", skills)
            }
            .addOnFailureListener { e ->
                Log.e("SkillRepoImpl", "getSkills error", e)
                callback(false, e.localizedMessage ?: "Failed to fetch skills", null)
            }
    }

    override fun updateSkill(
        userId: String,
        skillId: String,
        model: SkillModel,
        callback: (Boolean, String) -> Unit
    ) {
        skillsRef(userId).document(skillId).update(model.toMap())
            .addOnSuccessListener {
                callback(true, "Skill updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SkillRepoImpl", "updateSkill error", e)
                callback(false, e.localizedMessage ?: "Failed to update skill")
            }
    }

    override fun deleteSkill(
        userId: String,
        skillId: String,
        callback: (Boolean, String) -> Unit
    ) {
        skillsRef(userId).document(skillId).delete()
            .addOnSuccessListener {
                callback(true, "Skill deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SkillRepoImpl", "deleteSkill error", e)
                callback(false, e.localizedMessage ?: "Failed to delete skill")
            }
    }
}