package com.juanfbenitez.prueba.idealista.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class PropertyDetailUiState {
    object Loading : PropertyDetailUiState()
    data class Success(
        val detail: PropertyDetailDTO,
        val favorite: FavoriteEntity?
    ) : PropertyDetailUiState()
    data class Error(val message: String) : PropertyDetailUiState()
}

class PropertyDetailViewModel(
    private val repository: PropertyRepository,
    private val propertyCode: String
) : ViewModel() {

    private val _detail = MutableStateFlow<PropertyDetailDTO?>(null)
    private val _uiState = MutableStateFlow<PropertyDetailUiState>(PropertyDetailUiState.Loading)
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    init {
        observeData()
        loadDetail()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(_detail, repository.getAllFavorites()) { detail, favorites ->
                if (detail == null) {
                    PropertyDetailUiState.Loading
                } else {
                    val favorite = favorites.find { it.propertyCode == propertyCode }
                    PropertyDetailUiState.Success(detail, favorite)
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun loadDetail() {
        viewModelScope.launch {
            try {
                _uiState.value = PropertyDetailUiState.Loading
                val detail = repository.getPropertyDetail(propertyCode)
                _detail.value = detail
            } catch (e: Exception) {
                _uiState.value = PropertyDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(propertyCode)
        }
    }
}
