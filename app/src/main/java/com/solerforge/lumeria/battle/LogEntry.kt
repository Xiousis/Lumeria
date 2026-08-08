package com.solerforge.lumeria.battle

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString

data class LogEntry(
    val message: String,
    val color: Color = Color.LightGray,
    val annotatedMessage: AnnotatedString? = null
)
