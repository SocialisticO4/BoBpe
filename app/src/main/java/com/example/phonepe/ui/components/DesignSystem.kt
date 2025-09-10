package com.example.phonepe.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.phonepe.ui.theme.*

// Buttons
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary600,
            contentColor = White,
            disabledContainerColor = Primary100,
            disabledContentColor = Gray500
        )
    ) {
        Text(text = text, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary25,
            contentColor = Primary600,
            disabledContainerColor = Gray100,
            disabledContentColor = Gray500
        )
    ) {
        Text(text = text, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
    }
}

// Icon container
@Composable
fun IconContainer(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Primary25,
    contentColor: Color = Primary600,
    size: Int = 48
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = contentColor)
    }
}

// Cards
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    val container = if (highlighted) Primary25 else MaterialTheme.colorScheme.surface
    val borderColor = if (highlighted) Primary100 else Color.Transparent
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = container),
        border = androidx.compose.foundation.BorderStroke(
            width = if (highlighted) 1.dp else 0.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) { content() }
}

// Search Bar
@Composable
fun AppSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    leadingIcon: ImageVector = Icons.Default.Search
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = { Text(placeholder, color = Gray500) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Gray500) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Gray100,
            unfocusedContainerColor = Gray100,
            disabledContainerColor = Gray100,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Primary600
        )
    )
}

// Notification badge (green with white border) with subtle top-right offset
@Composable
fun NotificationBadge(count: Int, content: @Composable () -> Unit) {
    BadgedBox(badge = {
        Box(
            modifier = Modifier
                .offset(x = 4.dp, y = (-4).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Success600)
                .border(1.5.dp, White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$count", color = White, fontSize = 12.sp)
        }
    }) {
        content()
    }
}

// Press-scale interaction
@Composable
fun Modifier.pressScale(pressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100), label = "pressScale"
    )
    return this.scale(scale)
}

// Currency text with optical adjustments and tabular nums
@Composable
fun CurrencyText(amountText: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = "₹",
            style = TextStyle(
                fontSize = 48.sp * 0.85f,
                fontWeight = FontWeight.Bold,
                color = Gray900,
                baselineShift = BaselineShift(+0.4f),
                letterSpacing = 0.02.em
            )
        )
        Text(
            text = amountText,
            style = TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900,
                letterSpacing = 0.02.em,
                fontFeatureSettings = "tnum" // tabular numbers
            )
        )
    }
}

// Animated count-up for numbers
@Composable
fun AnimatedCount(target: Double, durationMs: Int = 600, formatter: (Double) -> String) {
    var start by remember { mutableStateOf(0.0) }
    var animTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(target) {
        start = 0.0
        animTrigger++
    }
    val animated by animateFloatAsState(
        targetValue = target.toFloat(),
        animationSpec = tween(durationMs), label = "count"
    )
    Text(text = formatter(animated.toDouble()))
}

// Animated currency display
@Composable
fun AnimatedCurrencyText(target: Double, durationMs: Int = 600) {
    val animated by animateFloatAsState(
        targetValue = target.toFloat(),
        animationSpec = tween(durationMillis = durationMs),
        label = "animatedCurrency"
    )
    val text = remember(animated) { String.format("%,.0f", animated) }
    CurrencyText(amountText = text)
}
