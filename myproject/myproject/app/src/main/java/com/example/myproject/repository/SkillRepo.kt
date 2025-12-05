package com.example.myproject.repository

import com.example.myproject.model.SkillModel

interface SkillRepo {

    fun addSkill(
        userId: String,
        model: SkillModel,
        callback: (Boolean, String) -> Unit
    )

    fun getSkills(
        userId: String,
        callback: (Boolean, String, List<SkillModel>?) -> Unit
    )

    fun updateSkill(
        userId: String,
        skillId: String,
        model: SkillModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteSkill(
        userId: String,
        skillId: String,
        callback: (Boolean, String) -> Unit
    )
}