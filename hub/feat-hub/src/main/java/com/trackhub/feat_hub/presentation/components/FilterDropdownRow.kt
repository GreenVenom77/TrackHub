package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.components.FilterDropdown
import com.trackhub.feat_hub.R

@Composable
fun FilterDropdownRow(
    categories: List<String> = emptyList(),
    manufacturers: List<String> = emptyList(),
    selectedCategory: String = stringResource(R.string.all_categories),
    selectedManufacturer: String = stringResource(R.string.all_manufacturers),
    onCategorySelected: (String) -> Unit = {},
    onManufacturerSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    showCategory: Boolean = true,
    showManufacturer: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Dropdown
        if (showCategory) {
            FilterDropdown(
                label = stringResource(R.string.category),
                items = categories,
                selectedItem = selectedCategory,
                defaultItem = stringResource(R.string.all_categories),
                onItemSelected = onCategorySelected,
                modifier = Modifier.width(180.dp)
            )
        }

        // Manufacturer Dropdown
        if (showManufacturer) {
            FilterDropdown(
                label = stringResource(R.string.manufacturer),
                items = manufacturers,
                selectedItem = selectedManufacturer,
                defaultItem = stringResource(R.string.all_manufacturers),
                onItemSelected = onManufacturerSelected,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}