package com.example.myproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myproject.model.RequestModel
import com.example.myproject.repository.RequestRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Represents the UI state for the Requests screen
data class RequestUiState(
    val isLoading: Boolean = false,
    val requests: List<RequestModel> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class RequestViewModel(private val repo: RequestRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestUiState())
    val uiState: StateFlow<RequestUiState> = _uiState

    // Load all requests for the current user
    fun getRequests(userId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        repo.getRequests(userId) { success, message, requests ->
            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    requests = requests ?: emptyList()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )
            }
        }
    }

    // Add a new request
    fun addRequest(userId: String, model: RequestModel, callback: (Boolean, String) -> Unit) {
        repo.addRequest(userId, model) { success, message ->
            callback(success, message)
            if (success) getRequests(userId)
        }
    }

    // Update an existing request
    fun updateRequest(
        userId: String,
        requestId: String,
        model: RequestModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateRequest(userId, requestId, model) { success, message ->
            callback(success, message)
            if (success) getRequests(userId)
        }
    }

    // Delete a request
    fun deleteRequest(userId: String, requestId: String) {
        repo.deleteRequest(userId, requestId) { success, message ->
            if (success) {
                getRequests(userId)
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