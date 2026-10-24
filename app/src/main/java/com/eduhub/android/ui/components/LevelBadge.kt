package com.eduhub.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.theme.*

@Composable
fun LevelBadge(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (difficulty.lowercase()) {
        "beginner" -> Pair(EduCyan500.copy(alpha = 0.18f), EduCyan400)
        "intermediate" -> Pair(EduSky500.copy(alpha = 0.18f), EduSky400)
        "advanced" -> Pair(EduViolet500.copy(alpha = 0.18f), EduViolet500)
        else -> Pair(EduEmerald500.copy(alpha = 0.18f), EduEmerald500)
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = difficulty.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}
