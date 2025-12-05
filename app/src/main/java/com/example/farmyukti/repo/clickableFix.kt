package com.example.farmyukti.repo

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

// Use the simplest name for easy replacement later
@Composable
fun Modifier.safeClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    onClick: () -> Unit
): Modifier = this.composed {
    clickable(
        // The key is using the correct overload:
        interactionSource = remember { MutableInteractionSource() },
        indication = LocalIndication.current, // Explicitly pass the current (modern) indication
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = onClick
    )
}