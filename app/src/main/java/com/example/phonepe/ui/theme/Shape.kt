package com.example.phonepe.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Corner radius hierarchy
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),   // badges
    small = RoundedCornerShape(8.dp),        // micro elements
    medium = RoundedCornerShape(12.dp),      // buttons, inputs
    large = RoundedCornerShape(16.dp),       // inner cards
    extraLarge = RoundedCornerShape(20.dp)   // outer containers
)

