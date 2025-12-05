package com.example.myproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myproject.model.SkillModel
import com.example.myproject.repository.SkillRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Represents the UI state for the Skills screen
data class SkillUiState(
    val isLoading: Boolean = false,
    val skills: List<SkillModel> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class SkillViewModel(private val repo: SkillRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillUiState())
    val uiState: StateFlow<SkillUiState> = _uiState

    // Load all skills for the current user
    fun getSkills(userId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        repo.getSkills(userId) { success, message, skills ->
            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    skills = skills ?: emptyList()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )
            }
        }
    }

    // Add a new skill
    fun addSkill(userId: String, model: SkillModel, callback: (Boolean, String) -> Unit) {
        repo.addSkill(userId, model) { success, message ->
            callback(success, message)
            if (success) getSkills(userId)
        }
    }

    // Update an existing skill
    fun updateSkill(
        userId: String,
        skillId: String,
        model: SkillModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateSkill(userId, skillId, model) { success, message ->
            callback(success, message)
            if (success) getSkills(userId)
        }
    }

    // Delete a skill
    fun deleteSkill(userId: String, skillId: String) {
        repo.deleteSkill(userId, skillId) { success, message ->
            if (success) {
                getSkills(userId)
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = message)
            }
        }
    }

    // Clear error/success messages after showing them
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}