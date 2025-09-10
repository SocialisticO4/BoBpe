package com.example.phonepe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.phonepe.data.AppDatabase
import com.example.phonepe.ui.HistoryViewModel as EventLoggingViewModel
import com.example.phonepe.ui.HistoryViewModelFactory as EventLoggingViewModelFactory
import com.example.phonepe.ui.screens.HistoryScreen
import com.example.phonepe.ui.screens.HomeScreen
import com.example.phonepe.ui.screens.PaymentScreen
import com.example.phonepe.ui.screens.ScannerScreen
import com.example.phonepe.ui.screens.SuccessScreen
import com.example.phonepe.ui.screens.TransactionDetailScreen
import com.example.phonepe.ui.screens.PlaceholderScreen

class MainActivity : ComponentActivity() {
    private val eventLoggingVm: EventLoggingViewModel by viewModels { EventLoggingViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = false // Keep false for light nav bar icons if bg is dark

        setContent {
            MaterialTheme {
                App(eventLoggingVm = eventLoggingVm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun App(eventLoggingVm: EventLoggingViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        currentRoute?.let { route ->
            eventLoggingVm.logEvent("visit", route)
        }
    }

    val context = LocalContext.current
    val db = remember(context) { AppDatabase.get(context) }
    val transactionHistoryVm: com.example.phonepe.viewmodel.HistoryViewModel = viewModel(
        factory = com.example.phonepe.viewmodel.HistoryViewModelFactory(db.transactionDao())
    )

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) { 
                HomeScreen(
                    onShowHistory = {
                        eventLoggingVm.logEvent("action", "open_history")
                        navController.navigate(Routes.HISTORY)
                    }
                )
            }
            // Routes for items previously in bottom nav but now removed, can be accessed otherwise or removed.
            // composable(Routes.SEARCH) { SearchScreen(navController) } 
            // composable(Routes.NOTIFICATIONS) { PlaceholderScreen(navController, "Notifications") } 
            
            composable(Routes.SCANNER) { ScannerScreen(navController) } 
            composable(Routes.HISTORY) { 
                HistoryScreen(
                    navController = navController, 
                    historyViewModel = transactionHistoryVm
                ) 
            }
            composable("payment/{name}/{upiId}?qrData={qrData}") { navBackStackEntry ->
                val name = navBackStackEntry.arguments?.getString("name").orEmpty()
                val upiId = navBackStackEntry.arguments?.getString("upiId").orEmpty()
                val qrData = navBackStackEntry.arguments?.getString("qrData")
                PaymentScreen(name = name, upiId = upiId, qrData = qrData, navController = navController, historyViewModel = transactionHistoryVm)
            }
            composable(Routes.SUCCESS) { 
                SuccessScreen(navController = navController, historyViewModel = transactionHistoryVm)
            }
            composable(
                route = Routes.TRANSACTION_DETAIL,
                arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val transactionId = navBackStackEntry.arguments?.getInt("transactionId")
                if (transactionId != null) {
                    TransactionDetailScreen(
                        navController = navController, 
                        historyViewModel = transactionHistoryVm, 
                        transactionId = transactionId
                    )
                } else {
                    navController.popBackStack()
                }
            }
            composable(Routes.PAY) { PlaceholderScreen(navController, "Pay Screen") }
            composable(Routes.REWARDS) { PlaceholderScreen(navController, "Rewards Screen") }
        }
    }
}

private object Routes {
    const val HOME = "home" 
    const val SCANNER = "scanner" 
    const val HISTORY = "history" 
    const val SUCCESS = "success"
    const val TRANSACTION_DETAIL = "transaction_detail/{transactionId}"
    const val PAY = "pay_screen"
    const val REWARDS = "rewards_screen"
    // Removed SEARCH and NOTIFICATIONS as they are no longer in bottom nav
}

@Composable
private fun BottomNavBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current

    val navItems = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Outlined.Home),
        BottomNavItem("Pay", Routes.PAY, Icons.Outlined.AccountBalanceWallet),
        BottomNavItem("Scan QR", Routes.SCANNER, Icons.Filled.QrCodeScanner, isFab = true),
        BottomNavItem("Rewards", Routes.REWARDS, Icons.Outlined.CardGiftcard),
        BottomNavItem("History", Routes.HISTORY, Icons.Outlined.History)
    )

    val bottomNavHeight = 60.dp
    val fabSize = 56.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomNavHeight + fabSize / 3) // Adjusted height to accommodate FAB overlap
            .background(Color.Transparent) // Main Box is transparent
    ) {
        // Actual Bottom Nav Bar background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(bottomNavHeight)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            color = Color(0xFFF0F0F0) // Light gray background as per screenshot
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    if (item.isFab) {
                        // Spacer for the FAB area
                        Spacer(modifier = Modifier.width(fabSize)) 
                    } else {
                        val isSelected = currentRoute == item.route
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null, // No ripple if haptic is primary feedback
                                    onClick = { 
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Or Click
                                        onNavigate(item.route)
                                    }
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Centered FAB (QR Scanner)
        val qrItem = navItems.first { it.isFab }
        FloatingActionButton(
            onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(qrItem.route) 
            },
            modifier = Modifier
                .align(Alignment.TopCenter) // Align to top of the Box, then offset
                .offset(y = (-fabSize / 3)) // Offset to make it sit above the bar
                .size(fabSize),
            shape = CircleShape,
            containerColor = Color(0xFF6200EE), // Purple color similar to screenshot
            contentColor = Color.White
        ) {
            Icon(
                imageVector = qrItem.icon,
                contentDescription = qrItem.label,
                modifier = Modifier.size(fabSize / 2)
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val isFab: Boolean = false
)

// PlaceholderScreen.kt (if not already created for previous steps, create a simple one)
// package com.example.phonepe.ui.screens
// 
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.navigation.NavController
// 
// @Composable
// fun PlaceholderScreen(navController: NavController, screenName: String) {
//     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//         Text(text = "$screenName - Placeholder")
//     }
// }
