@file:OptIn(ExperimentalMaterial3Api::class)

package com.greenvenom.core_ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.theme.AppTheme

@Composable
fun FilterDropdown(
    label: String,
    items: List<String>,
    selectedItem: String,
    defaultItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        FilterChip(
            selected = selectedItem != defaultItem,
            onClick = { expanded = true },
            label = {
                Text(
                    text = selectedItem,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = if (selectedItem != defaultItem) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else null,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier
                .height(40.dp),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedItem != defaultItem,
                borderWidth = 1.dp
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = defaultItem,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    onItemSelected(defaultItem)
                    expanded = false
                },
                leadingIcon = if (selectedItem == defaultItem) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else null
            )

            if (items.isNotEmpty()) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    leadingIcon = if (selectedItem == item) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun FilterDropdownPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filter Dropdown Examples",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Default state
            FilterDropdown(
                label = "Category",
                items = listOf("Electronics", "Furniture", "Food", "Clothing"),
                selectedItem = "All Categories",
                defaultItem = "All Categories",
                onItemSelected = {}
            )

            // Selected state
            FilterDropdown(
                label = "Manufacturer",
                items = listOf("Samsung", "LG", "Sony", "Apple"),
                selectedItem = "Samsung",
                defaultItem = "All Manufacturers",
                onItemSelected = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FilterDropdownDarkPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}