package com.flet.gshttp

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedButton(
    text: String,
    textColor: Color,
    bgColor: Color,
    scale: Float,
    onPressDown: () -> Unit,
    onPressUp: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(56.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    onPressDown()
                    tryAwaitRelease()
                    onPressUp()
                    onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
    }
}