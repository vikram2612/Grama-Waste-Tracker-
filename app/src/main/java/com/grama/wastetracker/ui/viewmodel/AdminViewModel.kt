package com.grama.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grama.wastetracker.data.model.TractorLocation
import com.grama.wastetracker.data.model.BlackspotReport
import com.grama.wastetracker.data.repository.BlackspotRepository
import com.grama.wastetracker.data.repository.TractorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val reports: List<BlackspotReport> = emptyList(),
    val tractorLocation: TractorLocation? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val totalReports: Int = 0,
    val pendingCount: Int = 0,
    val resolvedCount: Int = 0
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val tractorRepository: TractorRepository,
    private val blackspotRepository: BlackspotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            tractorRepository.observeTractorLocation().collect { location ->
                _uiState.update { it.copy(tractorLocation = location) }
            }
        }
        viewModelScope.launch {
            blackspotRepository.observeAllReports()
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { reports ->
                    _uiState.update {
                        it.copy(
                            reports = reports,
                            isLoading = false,
                            totalReports = reports.size,
                            pendingCount = reports.count { r -> r.status == BlackspotReport.STATUS_PENDING },
                            resolvedCount = reports.count { r -> r.status == BlackspotReport.STATUS_RESOLVED }
                        )
                    }
                }
        }
    }

    fun updateTractorLocation(lat: Double, lng: Double, driverName: String, routeName: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val location = TractorLocation(
                latitude = lat,
                longitude = lng,
                isActive = true,
                driverName = driverName,
                routeName = routeName,
                lastUpdated = System.currentTimeMillis()
            )
            tractorRepository.updateTractorLocation(location).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, updateSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun updateTractorStatus(isActive: Boolean) {
        viewModelScope.launch {
            tractorRepository.updateTractorStatus(isActive).fold(
                onSuccess = { },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    fun markReportResolved(reportId: String, adminId: String, notes: String = "") {
        viewModelScope.launch {
            blackspotRepository.markAsResolved(reportId, adminId, notes).fold(
                onSuccess = { },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
    fun clearSuccess() { _uiState.update { it.copy(updateSuccess = false) } }
}
