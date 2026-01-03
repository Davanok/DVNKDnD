package com.davanok.dvnkdnd.ui.pages.characterFull.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

private const val PREFERRED_IMAGE_WIDTH_DP = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    HorizontalMultiBrowseCarousel(
        modifier = modifier.height(PREFERRED_IMAGE_WIDTH_DP.dp),
        state = rememberCarouselState { images.size },
        preferredItemWidth = PREFERRED_IMAGE_WIDTH_DP.dp,
        itemSpacing = 12.dp
    ) { index ->
        AsyncImage(
            modifier = Modifier.fillMaxSize().maskClip(MaterialTheme.shapes.extraLarge),
            model = images[index],
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}