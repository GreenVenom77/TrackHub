package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.theme.AppTheme
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.models.UserSearchUI
import com.trackhub.feat_hub.presentation.utils.getStatusColor

@Composable
fun UserSearchItem(
    userUI: UserSearchUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userUI.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = userUI.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status badge
            Surface(
                color = userUI.currentStatus.getStatusColor().copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = stringResource(id = userUI.statusTextResId),
                    style = MaterialTheme.typography.labelSmall,
                    color = userUI.currentStatus.getStatusColor(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserSearchItemPreview() {
    AppTheme {
        UserSearchItem(
            userUI = UserSearchUI(
                userId = "1",
                displayName = "John Doe",
                email = "john.doe@example.com",
                statusTextResId = R.string.members,
                currentStatus = MemberStatus.Member
            )
        )
    }
}