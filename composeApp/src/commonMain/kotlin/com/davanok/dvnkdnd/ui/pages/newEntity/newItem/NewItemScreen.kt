package com.davanok.dvnkdnd.ui.pages.newEntity.newItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen(

) {
    val images = FileSystem.SYSTEM.list("C:\\MyFiles\\ArtistycCreativity\\assets\\dvnkdnd".toPath()).filter { it.name.endsWith("webp") }
    println(images)
    HorizontalMultiBrowseCarousel (
        modifier = Modifier.fillMaxWidth(),
        state = rememberCarouselState { images.size },
        preferredItemWidth = 600.dp
    ) { index ->
        AsyncImage(
            modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge),
            model = images[index],
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}