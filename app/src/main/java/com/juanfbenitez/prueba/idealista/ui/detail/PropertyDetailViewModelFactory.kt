package com.juanfbenitez.prueba.idealista.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository

class PropertyDetailViewModelFactory(
    private val repository: PropertyRepository,
    private val propertyCode: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyDetailViewModel::class.java)) {
            return PropertyDetailViewModel(repository, propertyCode) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
