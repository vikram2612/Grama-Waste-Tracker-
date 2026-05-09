package com.grama.wastetracker.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grama.wastetracker.data.model.BlackspotReport
import com.grama.wastetracker.data.repository.BlackspotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BlackspotUiState(
    val reports: List<BlackspotReport> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val submitSuccess: Boolean = false,
    val selectedImageUri: Uri? = null,
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0
)

@HiltViewModel
class BlackspotViewModel @Inject constructor(
    private val blackspotRepository: BlackspotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlackspotUiState())
    val uiState: StateFlow<BlackspotUiState> = _uiState.asStateFlow()

    fun observeAllReports() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            blackspotRepository.observeAllReports()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { reports ->
                    _uiState.update { it.copy(reports = reports, isLoading = false) }
                }
        }
    }

    fun observeUserReports(userId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            blackspotRepository.observeUserReports(userId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { reports ->
                    _uiState.update { it.copy(reports = reports, isLoading = false) }
                }
        }
    }

    fun setSelectedImage(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun setCurrentLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(currentLatitude = lat, currentLongitude = lng) }
    }

    fun submitReport(
        userId: String,
        userName: String,
        description: String,
        address: String = ""
    ) {
        val state = _uiState.value
        if (description.isBlank()) {
            _uiState.update { it.copy(error = "Please add a description") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val report = BlackspotReport(
                userId = userId,
                userName = userName,
                description = description,
                latitude = state.currentLatitude,
                longitude = state.currentLongitude,
                address = address
            )
            blackspotRepository.submitReport(report, state.selectedImageUri).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isSubmitting = false, submitSuccess = true, selectedImageUri = null)
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isSubmitting = false, error = e.localizedMessage ?: "Submit failed")
                    }
                }
            )
        }
    }

    fun markAsResolved(reportId: String, adminId: String, notes: String = "") {
        viewModelScope.launch {
            blackspotRepository.markAsResolved(reportId, adminId, notes).fold(
                onSuccess = { /* UI updates via realtime listener */ },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            )
        }
    }

    fun resetSubmitState() {
        _uiState.update { it.copy(submitSuccess = false, error = null, selectedImageUri = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
