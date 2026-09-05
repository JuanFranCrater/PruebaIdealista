package com.juanfbenitez.prueba.idealista.ui.detail.compose

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juanfbenitez.prueba.idealista.ui.detail.PropertyDetailViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

/** Exposes the Hilt-generated assisted factory for [PropertyDetailViewModel] to Compose code, which
 * cannot use constructor injection like the Fragments do. */
@EntryPoint
@InstallIn(ActivityComponent::class)
interface PropertyDetailViewModelFactoryEntryPoint {
    fun propertyDetailViewModelFactory(): PropertyDetailViewModel.Factory
}

/**
 * Compose panel that shows the property detail as a bottom sheet-style drawer on top of the
 * (XML) list screen, reusing the same [PropertyDetailScreen] asset used by the dedicated
 * full-screen destination so both presentations look identical.
 *
 * When [propertyCode] is null nothing is composed/laid out, so touches pass through to the
 * underlying list screen views.
 */
@Composable
fun PropertyDetailDrawer(
    propertyCode: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep rendering the last selected property while the exit animation plays out, instead of
    // blanking the content the instant propertyCode becomes null.
    val lastCode = remember { mutableStateOf(propertyCode) }
    LaunchedEffect(propertyCode) {
        if (propertyCode != null) lastCode.value = propertyCode
    }

    val visible = propertyCode != null

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            val code = lastCode.value
            if (code != null) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    val context = LocalContext.current
                    val assistedFactory = remember(context) {
                        EntryPointAccessors.fromActivity(
                            context as Activity,
                            PropertyDetailViewModelFactoryEntryPoint::class.java
                        ).propertyDetailViewModelFactory()
                    }
                    val detailViewModel: PropertyDetailViewModel = viewModel(
                        key = "drawer_detail_$code",
                        factory = PropertyDetailViewModel.provideFactory(assistedFactory, code)
                    )
                    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()
                    PropertyDetailScreen(
                        uiState = uiState,
                        onFavoriteClick = detailViewModel::toggleFavorite,
                        onBackClick = onDismiss
                    )
                }
            }
        }
    }
}
