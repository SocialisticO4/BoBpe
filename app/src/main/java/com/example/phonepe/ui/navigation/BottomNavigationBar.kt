package com.example.phonepe.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination

val PhonePePurple = Color(0xFF5F259F)

@Composable
fun AppBottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEachIndexed { _, item ->
                    if (item.isCentralButton) {
                        Box(modifier = Modifier.weight(1f)) { /* Empty space for FAB */ }
                    } else {
                        val route = item.screen.route
                        val label = item.screen.title
                        val selected = currentRoute == route
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = selected,
                                    onClick = {
                                        if (currentRoute != route) {
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = if (selected) item.selectedIconRes else item.unselectedIconRes),
                                contentDescription = label,
                                tint = if (selected) Color.Black else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (selected) Color.Black else Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Central FAB for QR Scanner
            val scannerItem = items.find { it.isCentralButton }
            if (scannerItem != null) {
                FloatingActionButton(
                    onClick = {
                        val route = scannerItem.screen.route
                        if (navController.currentDestination?.route != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-18).dp)
                        .size(56.dp),
                    shape = CircleShape,
                    containerColor = PhonePePurple,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = scannerItem.selectedIconRes),
                        contentDescription = scannerItem.screen.title,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
