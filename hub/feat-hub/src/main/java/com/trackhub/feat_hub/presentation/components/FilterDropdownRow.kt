package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    defaultCategory: String = stringResource(R.string.all_categories),
    defaultManufacturer: String = stringResource(R.string.all_manufacturers),
    onCategorySelected: (String?) -> Unit = {},
    onManufacturerSelected: (String?) -> Unit = {},
    modifier: Modifier = Modifier,
    showCategory: Boolean = true,
    showManufacturer: Boolean = true
) {
    var selectedCategory by rememberSaveable { mutableStateOf(defaultCategory) }
    var selectedManufacturer by rememberSaveable { mutableStateOf(defaultManufacturer) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Dropdown
        if (showCategory) {
            FilterDropdown(
                items = categories,
                selectedItem = selectedCategory,
                defaultItem = defaultCategory,
                onItemSelected = { category ->
                    onCategorySelected(if (category == defaultCategory) null else category)
                    selectedCategory = category
                },
                modifier = Modifier
                    .width(200.dp)
            )
        }

        // Manufacturer Dropdown
        if (showManufacturer) {
            FilterDropdown(
                items = manufacturers,
                selectedItem = selectedManufacturer,
                defaultItem = defaultManufacturer,
                onItemSelected = { manufacturer ->
                    onManufacturerSelected(if (manufacturer == defaultManufacturer) null else manufacturer)
                    selectedManufacturer = manufacturer
                },
                modifier = Modifier
                    .width(200.dp)
            )
        }
    }
}