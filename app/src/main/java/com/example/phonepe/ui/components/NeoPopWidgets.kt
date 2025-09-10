package com.example.phonepe.ui.components

import android.content.res.ColorStateList
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import club.cred.neopop.PopFrameLayout
import androidx.core.widget.ImageViewCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

@Composable
fun NeoPopButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density

    val leftPadding = (32 * density).toInt()
    val topPadding = (20 * density).toInt()
    val rightPadding = (32 * density).toInt()
    val bottomPadding = (20 * density).toInt()

    AndroidView(
        modifier = modifier,
        factory = {
            PopFrameLayout(context).apply {
                isClickable = true
                this.isEnabled = enabled
                setCenterSurfaceColor(backgroundColor.toArgb(), true)

                val tv = TextView(context).apply {
                    textSize = 16f
                    setTextColor(contentColor.toArgb())
                    text = label
                    isAllCaps = false
                    gravity = Gravity.CENTER
                    setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
                }
                addView(
                    tv,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                    )
                )
                setOnClickListener { onClick() }
            }
        },
        update = { view ->
            view.isEnabled = enabled
            view.setCenterSurfaceColor(backgroundColor.toArgb(), true)
            val child = view.getChildAt(0)
            if (child is TextView) {
                child.text = label
                child.setTextColor(contentColor.toArgb())
            }
        }
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun NeoPopNavItem(
    label: String,
    iconRes: Int,
    selectedIconRes: Int?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textBoldWhenSelected: Boolean = false,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    unselectedBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    // Control tinting separately for selected and unselected icons
    tintIconSelected: Boolean = false,
    tintIconUnselected: Boolean = true,
    tileWidthDp: Int = 84,
    tileHeightDp: Int = 76,
    iconSizeDp: Int = 0,
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density

    val currentBackgroundColor = if (selected) selectedBackgroundColor else unselectedBackgroundColor
    val currentContentColor = if (selected) selectedContentColor else unselectedContentColor
    val currentIconRes = if (selected && selectedIconRes != null) selectedIconRes else iconRes
    val applyTint = if (selected) tintIconSelected else tintIconUnselected

    val itemPaddingPx = (12 * density).toInt()
    val tileWidthPx = (tileWidthDp * density).toInt()
    val tileHeightPx = (tileHeightDp * density).toInt()
    val autoIconPx = (minOf(tileWidthPx, tileHeightPx) - 2 * itemPaddingPx).coerceAtLeast(16)
    val iconSizePx = if (iconSizeDp > 0) (iconSizeDp * density).toInt() else autoIconPx

    AndroidView(
        modifier = modifier,
        factory = {
            PopFrameLayout(context).apply {
                isClickable = true
                this.isEnabled = enabled
                setCenterSurfaceColor(currentBackgroundColor.toArgb(), true)
                setPadding(itemPaddingPx, itemPaddingPx, itemPaddingPx, itemPaddingPx)
                minimumWidth = tileWidthPx
                minimumHeight = tileHeightPx

                val iv = ImageView(context).apply {
                    // Load drawable so we can reliably tint vector or bitmap drawables
                    var d = ContextCompat.getDrawable(context, currentIconRes)
                    if (d != null) {
                        d = DrawableCompat.wrap(d).mutate()
                        if (applyTint) {
                            DrawableCompat.setTintList(d, ColorStateList.valueOf(currentContentColor.toArgb()))
                        } else {
                            DrawableCompat.setTintList(d, null)
                        }
                        setImageDrawable(d)
                    } else {
                        // fallback
                        setImageResource(currentIconRes)
                        ImageViewCompat.setImageTintList(this, if (applyTint) ColorStateList.valueOf(currentContentColor.toArgb()) else null)
                    }
                    scaleType = ScaleType.CENTER_INSIDE
                    layoutParams = FrameLayout.LayoutParams(iconSizePx, iconSizePx).apply {
                        gravity = Gravity.CENTER
                    }
                }
                addView(iv)
                setOnClickListener { onClick() }
            }
        },
        update = { view ->
            view.isEnabled = enabled
            val newBgColor = if (selected) selectedBackgroundColor else unselectedBackgroundColor
            val newContentColor = if (selected) selectedContentColor else unselectedContentColor
            val newIconRes = if (selected && selectedIconRes != null) selectedIconRes else iconRes
            val applyTint = if (selected) tintIconSelected else tintIconUnselected

            view.setCenterSurfaceColor(newBgColor.toArgb(), true)
            view.minimumWidth = tileWidthPx
            view.minimumHeight = tileHeightPx
            view.setPadding(itemPaddingPx, itemPaddingPx, itemPaddingPx, itemPaddingPx)

            val iv = view.getChildAt(0) as ImageView
            var newDrawable = ContextCompat.getDrawable(context, newIconRes)
            if (newDrawable != null) {
                newDrawable = DrawableCompat.wrap(newDrawable).mutate()
                if (applyTint) {
                    DrawableCompat.setTintList(newDrawable, ColorStateList.valueOf(newContentColor.toArgb()))
                } else {
                    DrawableCompat.setTintList(newDrawable, null)
                }
                iv.setImageDrawable(newDrawable)
            } else {
                iv.setImageResource(newIconRes)
                ImageViewCompat.setImageTintList(iv, if (applyTint) ColorStateList.valueOf(newContentColor.toArgb()) else null)
            }
            iv.scaleType = ScaleType.CENTER_INSIDE
            iv.layoutParams = FrameLayout.LayoutParams(iconSizePx, iconSizePx).apply {
                gravity = Gravity.CENTER
            }
        }
    )
}

@Composable
fun NeoPopFabItem(
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    tintIcon: Boolean = false,
    tileSizeDp: Int = 68,
    iconSizeDp: Int = 36,
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density

    val iconSizePx = (iconSizeDp * density).toInt()
    val paddingPx = (14 * density).toInt()
    val tileSizePx = (tileSizeDp * density).toInt()

    AndroidView(
        modifier = modifier,
        factory = {
            PopFrameLayout(context).apply {
                isClickable = true
                this.isEnabled = enabled
                setCenterSurfaceColor(backgroundColor.toArgb(), true)
                minimumWidth = tileSizePx
                minimumHeight = tileSizePx

                val iv = ImageView(context).apply {
                    var d = ContextCompat.getDrawable(context, iconRes)
                    if (d != null) {
                        d = DrawableCompat.wrap(d).mutate()
                        if (tintIcon) {
                            DrawableCompat.setTintList(d, ColorStateList.valueOf(contentColor.toArgb()))
                        } else {
                            DrawableCompat.setTintList(d, null)
                        }
                        setImageDrawable(d)
                    } else {
                        setImageResource(iconRes)
                        ImageViewCompat.setImageTintList(this, if (tintIcon) ColorStateList.valueOf(contentColor.toArgb()) else null)
                    }
                    layoutParams = FrameLayout.LayoutParams(iconSizePx, iconSizePx).apply {
                        gravity = Gravity.CENTER
                    }
                }
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                addView(iv)
                setOnClickListener { onClick() }
            }
        },
        update = { view ->
            view.isEnabled = enabled
            view.setCenterSurfaceColor(backgroundColor.toArgb(), true)
            view.minimumWidth = tileSizePx
            view.minimumHeight = tileSizePx
            val iv = view.getChildAt(0) as ImageView

            var d = ContextCompat.getDrawable(context, iconRes)
            if (d != null) {
                d = DrawableCompat.wrap(d).mutate()
                if (tintIcon) {
                    DrawableCompat.setTintList(d, ColorStateList.valueOf(contentColor.toArgb()))
                } else {
                    DrawableCompat.setTintList(d, null)
                }
                iv.setImageDrawable(d)
            } else {
                iv.setImageResource(iconRes)
                ImageViewCompat.setImageTintList(iv, if (tintIcon) ColorStateList.valueOf(contentColor.toArgb()) else null)
            }
        }
    )
}
