package com.example.phonepe

// import androidx.compose.foundation.background // Likely unused if Box only has Image background
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.phonepe.data.AppDatabase
import com.example.phonepe.ui.screens.HistoryScreen
import com.example.phonepe.ui.screens.HomeScreen
import com.example.phonepe.ui.screens.PaymentScreen
import com.example.phonepe.ui.screens.PlaceholderScreen
import com.example.phonepe.ui.screens.ScannerScreen
import com.example.phonepe.ui.screens.SuccessScreen
import com.example.phonepe.ui.screens.TransactionDetailScreen
import com.example.phonepe.ui.HistoryViewModel as EventLoggingViewModel
import com.example.phonepe.ui.HistoryViewModelFactory as EventLoggingViewModelFactory

class MainActivity : ComponentActivity() {
    private val eventLoggingVm: EventLoggingViewModel by viewModels { EventLoggingViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = false

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
            BottomNavBarV3(
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
                    navController = navController, 
                    onShowHistory = {
                        eventLoggingVm.logEvent("action", "open_history")
                        navController.navigate(Routes.HISTORY)
                    }
                )
            }
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
            composable(Routes.OFFERS) { PlaceholderScreen(navController, "Offers Screen") }
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
    const val OFFERS = "offers" 
}

data class BottomNavItemV3(
    val label: String,
    val route: String,
    @DrawableRes val iconRes: Int,
    @DrawableRes val selectedIconRes: Int?,
    val isFab: Boolean = false
)

@Composable
private fun BottomNavBarV3(currentRoute: String?, onNavigate: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current

    val navItems = listOf(
        BottomNavItemV3(
            label = "Home",
            route = Routes.HOME,
            iconRes = R.drawable.bottom_nav_home_normal,      
            selectedIconRes = R.drawable.bottom_nav_home_selected 
        ),
        BottomNavItemV3(
            label = "Pay",
            route = Routes.PAY,
            iconRes = R.drawable.bottom_nav_pay_normal, 
            selectedIconRes = R.drawable.bottom_nav_pay_selected 
        ),
        BottomNavItemV3(
            label = "Scan QR",
            route = Routes.SCANNER,
            iconRes = R.drawable.ic_nav_qr, 
            selectedIconRes = null, 
            isFab = true
        ),
        BottomNavItemV3(
            label = "Rewards",
            route = Routes.REWARDS,
            iconRes = R.drawable.bottom_nav_rewards_normal, 
            selectedIconRes = R.drawable.bottom_nav_rewards_selected 
        ),
        BottomNavItemV3(
            label = "History",
            route = Routes.HISTORY,
            iconRes = R.drawable.bottom_nav_history_normal,
            selectedIconRes = R.drawable.bottom_nav_history_selected
        )
    )

    val bottomNavHeight = 60.dp
    val fabSize = 56.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomNavHeight + fabSize / 3)
    ) {
        Image(
            painter = painterResource(id = R.drawable.nav_bar),
            contentDescription = "Bottom Navigation Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(bottomNavHeight)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                if (item.isFab) {
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
                                indication = null,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigate(item.route)
                                }
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        val iconToDisplay = if (isSelected && item.selectedIconRes != null) {
                            item.selectedIconRes
                        } else {
                            item.iconRes
                        }
                        Icon(
                            painter = painterResource(id = iconToDisplay),
                            contentDescription = item.label,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
                        )
                    }
                }
            }
        }

        val qrItem = navItems.first { it.isFab }
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onNavigate(qrItem.route)
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-fabSize / 3))
                .size(fabSize),
            shape = CircleShape,
            containerColor = Color(0xFF673AB7),
            contentColor = Color.White
        ) {
            Icon(
                painter = painterResource(id = qrItem.iconRes),
                contentDescription = qrItem.label,
                modifier = Modifier.size(fabSize / 2),
                tint = Color.White
            )
        }
    }
}
