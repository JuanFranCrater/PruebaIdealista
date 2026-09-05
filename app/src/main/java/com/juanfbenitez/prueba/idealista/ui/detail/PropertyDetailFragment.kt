package com.juanfbenitez.prueba.idealista.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.juanfbenitez.prueba.idealista.di.Dependencies
import com.juanfbenitez.prueba.idealista.ui.detail.compose.PropertyDetailScreen
import com.juanfbenitez.prueba.idealista.ui.theme.PruebaIdealistaTheme

/**
 * Dedicated full-screen presentation of the property detail. The screen itself is entirely
 * Compose: this fragment only hosts a [ComposeView] and wires it to [PropertyDetailViewModel],
 * delegating all rendering to the [PropertyDetailScreen] asset shared with the drawer
 * presentation embedded in the list screen.
 */
class PropertyDetailFragment : Fragment() {

    private val args: PropertyDetailFragmentArgs by navArgs()
    private val viewModel: PropertyDetailViewModel by viewModels {
        PropertyDetailViewModelFactory(Dependencies.providePropertyRepository(requireContext()), args.propertyCode)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PruebaIdealistaTheme {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    PropertyDetailScreen(
                        uiState = uiState,
                        onFavoriteClick = viewModel::toggleFavorite,
                        onBackClick = { findNavController().navigateUp() }
                    )
                }
            }
        }
    }
}
