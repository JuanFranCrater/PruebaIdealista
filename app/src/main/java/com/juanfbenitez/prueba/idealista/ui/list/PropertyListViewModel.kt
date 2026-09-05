package com.juanfbenitez.prueba.idealista.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class PropertyListUiState {
    object Loading : PropertyListUiState()
    data class Success(val properties: List<PropertyItemUiModel>) : PropertyListUiState()
    data class Error(val message: String) : PropertyListUiState()
}

data class PropertyItemUiModel(
    val property: PropertyDTO,
    val isFavorite: Boolean,
    val dateFavorited: Long? = null
)

object PropertyOperation {
    const val SALE = "sale"
    const val RENT = "rent"
}

class PropertyListViewModel(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _properties = MutableStateFlow<List<PropertyDTO>?>(null)
    private val _selectedOperation = MutableStateFlow(PropertyOperation.SALE)
    val selectedOperation: StateFlow<String> = _selectedOperation.asStateFlow()
    private val _uiState = MutableStateFlow<PropertyListUiState>(PropertyListUiState.Loading)
    val uiState: StateFlow<PropertyListUiState> = _uiState.asStateFlow()

    init {
        observeData()
        loadProperties()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                _properties,
                repository.getAllFavorites(),
                _selectedOperation
            ) { properties, favorites, operation ->
                if (properties == null) {
                    PropertyListUiState.Loading
                } else {
                    val favoritesMap = favorites.associateBy { it.propertyCode }
                    val uiModels = properties
                        .filter { it.operation == operation }
                        .map { property ->
                            val favorite = favoritesMap[property.propertyCode]
                            PropertyItemUiModel(
                                property = property,
                                isFavorite = favorite != null,
                                dateFavorited = favorite?.dateFavorited
                            )
                        }
                    PropertyListUiState.Success(uiModels)
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun loadProperties() {
        viewModelScope.launch {
            try {
                val properties = repository.getPropertyList()
                _properties.value = properties
            } catch (e: Exception) {
                _uiState.value = PropertyListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectOperation(operation: String) {
        _selectedOperation.value = operation
    }

    fun toggleFavorite(propertyCode: String) {
        viewModelScope.launch {
            repository.toggleFavorite(propertyCode)
        }
    }
}
