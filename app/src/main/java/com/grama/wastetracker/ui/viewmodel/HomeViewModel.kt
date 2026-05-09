package com.grama.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grama.wastetracker.data.model.TractorLocation
import com.grama.wastetracker.data.repository.TractorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

data class HomeUiState(
    val tractorLocation: TractorLocation? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val estimatedMinutes: Int? = null,
    val userLat: Double = 0.0,
    val userLng: Double = 0.0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tractorRepository: TractorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeTractor()
    }

    private fun observeTractor() {
        viewModelScope.launch {
            tractorRepository.observeTractorLocation()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { location ->
                    val estimated = if (location != null && _uiState.value.userLat != 0.0) {
                        calculateETA(
                            location.latitude, location.longitude,
                            _uiState.value.userLat, _uiState.value.userLng
                        )
                    } else null
                    _uiState.update {
                        it.copy(
                            tractorLocation = location,
                            isLoading = false,
                            estimatedMinutes = estimated
                        )
                    }
                }
        }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(userLat = lat, userLng = lng) }
        // Recalculate ETA
        val tractor = _uiState.value.tractorLocation
        if (tractor != null) {
            val eta = calculateETA(tractor.latitude, tractor.longitude, lat, lng)
            _uiState.update { it.copy(estimatedMinutes = eta) }
        }
    }

    private fun calculateETA(
        tractorLat: Double, tractorLng: Double,
        userLat: Double, userLng: Double
    ): Int {
        val distance = haversineDistance(tractorLat, tractorLng, userLat, userLng)
        // Assume average speed of 20 km/h for a tractor in village roads
        val speedKmH = 20.0
        val timeHours = distance / speedKmH
        return (timeHours * 60).toInt().coerceAtLeast(1)
    }

    private fun haversineDistance(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val r = 6371.0 // Earth's radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
