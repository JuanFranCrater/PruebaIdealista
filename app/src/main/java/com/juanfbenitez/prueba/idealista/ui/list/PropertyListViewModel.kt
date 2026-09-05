package com.juanfbenitez.prueba.idealista.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.usecase.GetAllFavoritesUseCase
import com.juanfbenitez.prueba.idealista.domain.usecase.GetPropertyListUseCase
import com.juanfbenitez.prueba.idealista.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PropertyListUiState {
    object Loading : PropertyListUiState()
    data class Success(val properties: List<PropertyItemUiModel>) : PropertyListUiState()
    data class Error(val message: String) : PropertyListUiState()
}

data class PropertyItemUiModel(
    val property: Property,
    val isFavorite: Boolean,
    val dateFavorited: Long? = null
)

object PropertyOperation {
    const val SALE = "sale"
    const val RENT = "rent"
}

@HiltViewModel
class PropertyListViewModel @Inject constructor(
    private val getPropertyListUseCase: GetPropertyListUseCase,
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _properties = MutableStateFlow<List<Property>?>(null)
    private val _loadError = MutableStateFlow<String?>(null)
    private val _selectedOperation = MutableStateFlow(PropertyOperation.SALE)
    val selectedOperation: StateFlow<String> = _selectedOperation.asStateFlow()

    private val _selectedPropertyForDrawer = MutableStateFlow<String?>(null)

    /** Property code currently shown in the detail drawer panel, or null when it's closed. */
    val selectedPropertyForDrawer: StateFlow<String?> = _selectedPropertyForDrawer.asStateFlow()

    init {
        loadProperties()
    }

    /**
     * Builds a per-tab/page state stream filtered to [operation], so each page of the
     * swipeable ViewPager2 (Buy/Rent) observes only the properties it needs to show.
     */
    fun observeProperties(operation: String): Flow<PropertyListUiState> =
        combine(_properties, getAllFavoritesUseCase(), _loadError) { properties, favorites, error ->
            when {
                error != null -> PropertyListUiState.Error(error)
                properties == null -> PropertyListUiState.Loading
                else -> {
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
            }
        }

    fun loadProperties() {
        viewModelScope.launch {
            try {
                val properties = getPropertyListUseCase()
                _properties.value = properties
            } catch (e: Exception) {
                _loadError.value = e.message ?: "Unknown error"
            }
        }
    }

    fun selectOperation(operation: String) {
        _selectedOperation.value = operation
    }

    fun toggleFavorite(propertyCode: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(propertyCode)
        }
    }

    /** Opens the detail drawer panel for [propertyCode] (used in [com.juanfbenitez.prueba.idealista.data.prefs.DetailDisplayMode.DRAWER] mode). */
    fun selectPropertyForDrawer(propertyCode: String) {
        _selectedPropertyForDrawer.value = propertyCode
    }

    /** Closes the detail drawer panel. */
    fun clearDrawerSelection() {
        _selectedPropertyForDrawer.value = null
    }
}
