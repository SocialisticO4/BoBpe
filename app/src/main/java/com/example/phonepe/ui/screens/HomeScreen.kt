package com.example.phonepe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phonepe.R // Ensure R is imported for your drawables

// Colors (ensure these match your theme)
val BannerGradientStart = Color(0xFF581C87)
val BannerGradientEnd = Color(0xFF4C1D95)
val BannerYellow = Color(0xFFFACC15)
val PhonePePurple = Color(0xFF673AB7)
val PhonePeLightPurple = Color(0xFFEDE7F6)
val LightGrayBackground = Color(0xFFF5F5F5)
val TextColorPrimary = Color.Black
val TextColorSecondary = Color.Gray
val AccentColor = PhonePePurple
val FeatureBgBlue50 = Color(0xFFEFF6FF)
val FeatureBgPink50 = Color(0xFFFEF2F2)
val FeatureBgYellow50 = Color(0xFFFFFBEB)
val FeatureBgGreen50 = Color(0xFFF0FDF4)
val FeatureBgPurple50 = Color(0xFFFAF5FF)
val BadgeBgPink500 = Color(0xFFEC4899)
val BadgeBgGreen500 = Color(0xFF22C55E)

private object ScreenRoutes {
    const val OFFERS = "offers"
}

data class IconActionItem(
    val label: String,
    val iconResId: Int,
    val onClick: () -> Unit
)

data class FeatureCardItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val illustrationResId: Int,
    val backgroundColor: Color,
    val badge: String? = null,
    val badgeColor: Color? = null,
    val badgeTextColor: Color = Color.White,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    navController: NavController,
    onShowHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
    ) {
        item {
            PromotionalBannerNew(
                onClick = { navController.navigate(ScreenRoutes.OFFERS) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SectionTitle(title = "Money Transfers")
            MoneyTransferRow(
                items = listOf(
                    IconActionItem("To Mobile Number", R.drawable.ic_person, onClick = { /* TODO: navController.navigate(...) */ }),
                    IconActionItem("To Bank & Self A/C", R.drawable.ic_bank_building, onClick = { /* TODO: navController.navigate(...) */ }),
                    IconActionItem("Check Balance", R.drawable.ic_check_balance, onClick = { /* TODO: navController.navigate(...) */ })
                )
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            SectionTitle(title = "Recharge & Bills", actionText = "View All", onActionClick = { /* TODO: navController.navigate(...) to a 'View All Recharges' screen */ })
            RechargeAndBillsRow(
                items = listOf(
                    IconActionItem("Mobile Recharge", R.drawable.ic_mobile_recharge, onClick = { /* TODO: navController.navigate(...) */ }),
                    IconActionItem("Fees via Credit Card", R.drawable.ic_dth, onClick = { /* TODO: navController.navigate(...) */ }),
                    IconActionItem("Electricity Bill", R.drawable.ic_electricity, onClick = { /* TODO: navController.navigate(...) */ }),
                    IconActionItem("Loan Repayment", R.drawable.ic_loan_repayment_bag, onClick = { /* TODO: navController.navigate(...) */ })
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            val featureItems = listOf(
                FeatureCardItem("loans", "Loans", "Personal, Gold and More", R.drawable.img_feature_loans, FeatureBgBlue50, onClick = { /* TODO: navController.navigate("feature/loans") */ }),
                FeatureCardItem("insurance", "Insurance", null, R.drawable.img_feature_insurance, FeatureBgPink50, badge = "Offer", badgeColor = BadgeBgPink500, onClick = { /* TODO: navController.navigate("feature/insurance") */ }),
                FeatureCardItem("digital-gold", "Digital Gold", "Save ₹10 daily", R.drawable.img_feature_digital_gold, FeatureBgYellow50, onClick = { /* TODO: navController.navigate("feature/digital-gold") */ }),
                FeatureCardItem("travel", "Travel & Transit", "Flight, Train, Bus, Hotel, Metro", R.drawable.img_feature_travel, FeatureBgGreen50, badge = "Sale", badgeColor = BadgeBgGreen500, onClick = { /* TODO: navController.navigate("feature/travel") */ }),
                FeatureCardItem("mutual-funds", "Mutual Funds", "SIPs & Investments", R.drawable.img_feature_mutual_funds, FeatureBgPurple50, onClick = { /* TODO: navController.navigate("feature/mutual-funds") */ })
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(((featureItems.size + 1) / 2 * 165).dp), // Slightly adjusted height per row
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(featureItems, key = { it.id }) { item ->
                    SingleFeatureCardNew(item = item)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PromotionalBannerNew(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted vertical padding for banner position
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BannerGradientStart, BannerGradientEnd)
                )
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp) // Increased vertical padding inside banner
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp) // Slightly smaller P logo circle
                            .background(Color.White, CircleShape)
                            .padding(1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("P", fontWeight = FontWeight.Bold, color = BannerGradientEnd, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("PhonePe", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Cashback", color = BannerYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Fest 🎊", color = BannerYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(10.dp))

                Text("Up to ₹100 on", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Normal) // Adjusted weight
                Text("every payment", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Normal) // Adjusted weight

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClick() } 
                ) {
                    Text("Know more", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(22.dp) // Slightly smaller arrow circle
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ChevronRight, contentDescription = "Know more", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("*T&C Apply", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.img_banner_cashback_coins), // Ensure this image is in res/drawable
                contentDescription = "Cashback illustration",
                modifier = Modifier
                    .size(90.dp) // Slightly larger image
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun SingleFeatureCardNew(item: FeatureCardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }, 
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = item.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Reduced elevation slightly
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) { // Try to make cards in a row same height
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp), // Adjusted padding
                verticalArrangement = Arrangement.SpaceBetween // Push image to bottom
            ) {
                Column(modifier = Modifier.defaultMinSize(minHeight = 60.dp)) { 
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                        color = TextColorPrimary, 
                        fontSize = 14.sp, 
                        modifier = Modifier.padding(bottom = if (item.subtitle.isNullOrEmpty()) 8.dp else 4.dp)
                    )
                    item.subtitle?.let {
                        if (it.isNotEmpty()) {
                            Text(
                                text = it,
                                fontSize = 12.sp, 
                                color = TextColorSecondary, 
                                lineHeight = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp)) 
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = item.illustrationResId), // Ensure these images are in res/drawable
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(48.dp) 
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item.badge?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp) 
                        .background(item.badgeColor ?: AccentColor, RoundedCornerShape(20.dp)) // More rounded badge
                        .padding(horizontal = 8.dp, vertical = 3.dp) 
                ) {
                    Text(
                        text = it,
                        color = item.badgeTextColor,
                        fontSize = 9.sp, // Slightly smaller badge text
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, actionText: String? = null, onActionClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Increased vertical padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextColorPrimary
        )
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick, contentPadding = PaddingValues(0.dp)) {
                Text(text = actionText, color = AccentColor, fontWeight = FontWeight.SemiBold) // Bolder action text
            }
        }
    }
}

@Composable
fun MoneyTransferRow(items: List<IconActionItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    ) {
        items.forEach { item ->
            CircleIconActionItem(item = item, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun RechargeAndBillsRow(items: List<IconActionItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    ) {
        items.forEach { item ->
            CircleIconActionItem(item = item, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CircleIconActionItem(item: IconActionItem, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { item.onClick() } 
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp) // Slightly smaller circle for icons
                .clip(CircleShape)
                .background(PhonePeLightPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.iconResId),
                contentDescription = item.label,
                // DIAGNOSTIC STEP: Use Color.Unspecified to see original XML colors
                // If icons appear (e.g. black/white), then PhonePePurple tint was the issue.
                // If still no icons, the XML files or paths are likely the problem.
                tint = Color.Unspecified, // PhonePePurple, 
                modifier = Modifier.size(26.dp) // Slightly smaller icon size
            )
        }
        Spacer(modifier = Modifier.height(6.dp)) // Adjusted spacer
        Text(
            text = item.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextColorPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}
