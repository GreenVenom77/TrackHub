package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.greenvenom.core_ui.components.buttons.FilterDropdown
import com.trackhub.feat_hub.presentation.enums.InStockOptions

@Composable
fun FilterDropdownRow(
    categories: List<String>,
    manufacturers: List<String>,
    defaultCategory: String,
    selectedCategory: String,
    defaultManufacturer: String,
    selectedManufacturer: String,
    onCategorySelected: (String?) -> Unit,
    onManufacturerSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    showCategory: Boolean = true,
    showManufacturer: Boolean = true,
    selectedInStock: Boolean? = null,
    onInStockSelected: (Boolean?) -> Unit = {},
    showInStockFilter: Boolean = true
) {
    var selectedCategory by rememberSaveable { mutableStateOf(selectedCategory) }
    var selectedManufacturer by rememberSaveable { mutableStateOf(selectedManufacturer) }
    var selectedInStockOption by rememberSaveable { mutableStateOf(InStockOptions.fromValue(selectedInStock)) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            )
        }

        // InStock Dropdown
        if (showInStockFilter) {
            FilterDropdown(
                items = InStockOptions.entries.filter { it != InStockOptions.ALL }.map {
                    stringResource(it.label)
                },
                selectedItem = stringResource(selectedInStockOption.label),
                defaultItem = stringResource(InStockOptions.ALL.label),
                onItemSelected = { _, key ->
                    onInStockSelected(key?.value)
                    selectedInStockOption = key ?: InStockOptions.ALL
                },
                keys = InStockOptions.entries.filter { it != InStockOptions.ALL },
                modifier = Modifier
            )
        }
    }
}