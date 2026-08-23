package com.example.ui.components

import androidx.compose.runtime.Composable

/**
 * ParentalGateDialog
 *
 * Backwards-compatible alias for ParentalGate.
 */
@Composable
fun ParentalGateDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    title: String = "Grown-Ups Only"
) {
    ParentalGate(
        onDismiss = onDismiss,
        onSuccess = onSuccess,
        title = title
    )
}
