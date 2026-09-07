package com.juanfbenitez.prueba.idealista.ui.detail.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.juanfbenitez.prueba.idealista.R
import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.PropertyCharacteristics
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import com.juanfbenitez.prueba.idealista.ui.detail.PropertyDetailUiState
import com.juanfbenitez.prueba.idealista.ui.theme.IdealistaBadgeLime
import com.juanfbenitez.prueba.idealista.ui.theme.IdealistaDivider
import com.juanfbenitez.prueba.idealista.ui.theme.IdealistaGray
import com.juanfbenitez.prueba.idealista.ui.theme.IdealistaGrayDark
import com.juanfbenitez.prueba.idealista.ui.theme.IdealistaPrimary
import com.juanfbenitez.prueba.idealista.ui.theme.IdealistaSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HeaderHeight = 300.dp
private val FabSize = 56.dp

/**
 * Reusable Compose asset rendering the property detail screen. Used both by the dedicated
 * full-screen destination ([com.juanfbenitez.prueba.idealista.ui.detail.PropertyDetailFragment])
 * and by the drawer panel embedded in the list screen, so both presentations stay visually
 * consistent and share the same implementation.
 *
 * @param onBackClick when non-null, a back/close affordance is shown (navigate up on full
 * screen, dismiss the drawer when embedded).
 */
@Composable
fun PropertyDetailScreen(
    uiState: PropertyDetailUiState,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is PropertyDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is PropertyDetailUiState.Error -> {
                    Text(
                        text = uiState.message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }

                is PropertyDetailUiState.Success -> {
                    PropertyDetailSuccessContent(
                        detail = uiState.detail,
                        favorite = uiState.favorite,
                        onFavoriteClick = onFavoriteClick
                    )
                }
            }

            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.35f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyDetailSuccessContent(
    detail: PropertyDetail,
    favorite: Favorite?,
    onFavoriteClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { PropertyImagePager(images = detail.images) }
        item {
            PropertyDetailBody(
                detail = detail,
                favorite = favorite,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
private fun PropertyImagePager(images: List<String>) {
    if (images.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        return
    }
    val pagerState = rememberPagerState(pageCount = { images.size })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = stringResource(R.string.property_image),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                modifier = Modifier.fillMaxSize()
            )
        }

        if (images.size > 1) {
            PagerIndicator(
                pageCount = images.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun PagerIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (selected) 8.dp else 6.dp)
                    .background(
                        color = if (selected) Color.White else Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun PropertyDetailBody(
    detail: PropertyDetail,
    favorite: Favorite?,
    onFavoriteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val characteristics: PropertyCharacteristics? = detail.characteristics
    var showRemoveFavoriteDialog by remember { mutableStateOf(false) }

    if (showRemoveFavoriteDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveFavoriteDialog = false },
            title = { Text(stringResource(R.string.remove_favorite_title)) },
            text = { Text(stringResource(R.string.remove_favorite_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveFavoriteDialog = false
                    onFavoriteClick()
                }) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveFavoriteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (favorite != null) {
            Surface(
                color = IdealistaBadgeLime,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.favorited_on,
                        dateFormat.format(Date(favorite.dateFavorited))
                    ),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.price_format,
                        detail.price.amount,
                        detail.price.currencySuffix
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = detail.propertyType.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = IdealistaGrayDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Fixed in place under the photo (part of the scrolling content), not a floating
            // action button, so it doesn't overlap the image or content while scrolling.
            IconButton(
                onClick = {
                    if (favorite != null) {
                        showRemoveFavoriteDialog = true
                    } else {
                        onFavoriteClick()
                    }
                },
                modifier = Modifier
                    .size(FabSize)
                    .background(
                        color = IdealistaPrimary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (favorite != null) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(
                        if (favorite != null) R.string.remove_from_favorites else R.string.add_to_favorites
                    ),
                    tint = Color.Black
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = IdealistaDivider)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatColumn(
                value = characteristics?.rooms?.toString() ?: "-",
                label = stringResource(R.string.rooms)
            )
            StatColumn(
                value = characteristics?.bathrooms?.toString() ?: "-",
                label = stringResource(R.string.bathrooms)
            )
            StatColumn(
                value = stringResource(
                    R.string.area_format,
                    characteristics?.constructedArea?.toFloat() ?: 0f
                ),
                label = stringResource(R.string.size)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = IdealistaDivider)

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        ExpandableDescription(
            text = detail.description.orEmpty(),
            modifier = Modifier.padding(top = 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = IdealistaDivider)

        Text(
            text = stringResource(R.string.characteristics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Column(modifier = Modifier.padding(top = 8.dp)) {
            val yes = stringResource(R.string.yes)
            val no = stringResource(R.string.no)
            characteristics?.let {
                CharacteristicLine(stringResource(R.string.lift), if (it.hasLift == true) yes else no)
                CharacteristicLine(stringResource(R.string.exterior), if (it.isExterior == true) yes else no)
                it.status?.let { status -> CharacteristicLine(stringResource(R.string.status), status) }
                it.floor?.let { floor -> CharacteristicLine(stringResource(R.string.floor), floor) }
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentSize()
    ) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = IdealistaGray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun CharacteristicLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        color = IdealistaGrayDark,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

private const val CollapsedDescriptionMaxLines = 10

/**
 * Description text that collapses to [CollapsedDescriptionMaxLines] lines when it's longer than
 * that, showing a "Show more"/"Show less" toggle to expand/collapse it.
 */
@Composable
private fun ExpandableDescription(text: String, modifier: Modifier = Modifier) {
    var expanded by remember(text) { mutableStateOf(false) }
    var isOverflowing by remember(text) { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = IdealistaGrayDark,
            maxLines = if (expanded) Int.MAX_VALUE else CollapsedDescriptionMaxLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            onTextLayout = { result ->
                if (!expanded) {
                    isOverflowing = result.hasVisualOverflow
                }
            }
        )

        if (isOverflowing || expanded) {
            Text(
                text = stringResource(
                    if (expanded) R.string.show_less else R.string.show_more
                ),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = IdealistaSecondary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}
