package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    name: String?,
    email: String?,
    onClick: () -> Unit = {},
) {
    val isLoading = name.isNullOrEmpty() || email.isNullOrEmpty()
    Card(
        modifier = modifier.height(90.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!isLoading) {
                TextContent(
                    name = name,
                    email = email,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )
                LogoutButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = onClick
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                        .size(48.dp)
                        .height(90.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileCardPreview() {
    ProfileCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        name = "John Doe",
        email = "john.doe@example.com",
        onClick = {},
    )
}


@Preview
@Composable
fun ProfileCardErrorPreview() {
    ProfileCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        name = "",
        email = "john.doe@example.com",
        onClick = {},
    )
}


@Composable
fun TextContent(
    name: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.padding(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Text(
            text = "Logout",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}


@Composable
fun CircularPicture(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )
}