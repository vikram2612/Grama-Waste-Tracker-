package com.grama.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grama.wastetracker.data.model.WasteGuideItem
import com.grama.wastetracker.data.repository.WasteGuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WasteGuideUiState(
    val guides: List<WasteGuideItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedCategory: String? = null
)

@HiltViewModel
class WasteGuideViewModel @Inject constructor(
    private val wasteGuideRepository: WasteGuideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WasteGuideUiState())
    val uiState: StateFlow<WasteGuideUiState> = _uiState.asStateFlow()

    init {
        loadGuides()
    }

    private fun loadGuides() {
        viewModelScope.launch {
            wasteGuideRepository.getWasteGuides()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { guides ->
                    _uiState.update { it.copy(guides = guides, isLoading = false) }
                }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
