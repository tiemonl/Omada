package dev.tiemonliam.omada.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Error(
    modifier: Modifier = Modifier,
    message: String
) {
    Box(
        modifier = modifier
            .testTag(ErrorTestTag.ERROR)
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = message,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

object ErrorTestTag {
    const val ERROR = "error"
}