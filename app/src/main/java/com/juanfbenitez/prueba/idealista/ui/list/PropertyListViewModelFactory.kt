package com.juanfbenitez.prueba.idealista.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository

class PropertyListViewModelFactory(
    private val repository: PropertyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyListViewModel::class.java)) {
            return PropertyListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
