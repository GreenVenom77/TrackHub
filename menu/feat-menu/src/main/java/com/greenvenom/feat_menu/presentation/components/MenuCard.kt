package com.greenvenom.feat_menu.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackhub.feat_menu.R

@Composable
fun MenuCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    iconDescription: String? = null,
    titleStyle: TextStyle = LocalTextStyle.current,
    contentColor: Color = LocalContentColor.current
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            painter?.let {
                Icon(
                    painter = painter,
                    contentDescription = iconDescription,
                    modifier = Modifier.size(56.dp),
                    tint = contentColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = titleStyle.copy(color = contentColor)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MenuCardPreview() {
    MenuCard(
        title = "Profile",
        onClick = {},
        painter = painterResource(R.drawable.person_circle_ic),
        iconDescription = stringResource(R.string.profile_icon),
        titleStyle = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold
        )
    )
}