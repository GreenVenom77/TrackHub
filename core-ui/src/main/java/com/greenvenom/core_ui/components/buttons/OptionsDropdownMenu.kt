@file:OptIn(ExperimentalMaterial3Api::class)

package com.greenvenom.core_ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.R

@Composable
fun OptionsDropdownMenu(
    modifier: Modifier = Modifier,
    optionsContent: @Composable ColumnScope.(onDismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.options),
                modifier = Modifier.size(32.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(180.dp)
        ) {
            optionsContent { expanded = false }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OptionsDropdownMenuPreview() {
    OptionsDropdownMenu(
        optionsContent = {
            DropdownMenuItem(
                text = { Text("Option 1") },
                onClick = { }
            )
            DropdownMenuItem(
                text = { Text("Option 2") },
                onClick = { }
            )
        }
    )
}
