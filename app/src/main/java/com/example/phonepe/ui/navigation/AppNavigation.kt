package com.example.phonepe.ui.navigation

import androidx.annotation.DrawableRes
import com.example.phonepe.R

sealed class AppScreen(val route: String, val title: String) {
    object Home : AppScreen("home", "Home")
    object Search : AppScreen("search", "Search")
    object Scanner : AppScreen("scanner", "Scanner")
    object Alerts : AppScreen("alerts", "Alerts")
    object History : AppScreen("history", "History")
}

data class BottomNavItem(
    val screen: AppScreen,
    @DrawableRes val selectedIconRes: Int,
    @DrawableRes val unselectedIconRes: Int,
    val isCentralButton: Boolean = false
)
