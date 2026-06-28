package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackhub.core_hub.domain.enums.BaseUnit
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.models.ItemUI

@Composable
fun ItemListCard(
    hubItem: ItemUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(12.dp),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = hubItem.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = hubItem.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${hubItem.stockCount} ${stringResource(hubItem.unit.displayNameRes)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = hubItem.updatedAt ?: hubItem.createdAt,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ItemPreview() {
    ItemListCard(
        hubItem = ItemUI(
            id = "1",
            hubId = "1",
            name = "Test Item",
            stockCount = "10",
            unit = BaseUnit.PIECES,
            imageUrl = "",
            createdAt = "March 9, 2025 at 5:30 PM",
            updatedAt = "March 9, 2025 at 5:30 PM",
            manufacturer = "Test Manufacturer",
            category = "Test Category",
            inStock = true
        ),
        onClick = {  },
        modifier = Modifier
    )
}