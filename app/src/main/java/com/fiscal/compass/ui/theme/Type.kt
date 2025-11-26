package com.fiscal.compass.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fiscal.compass.R

// Set of Material typography styles to start with

val GilroyFontFamily = FontFamily(
    Font(R.font.gilroy_black, FontWeight.Black),
    Font(R.font.gilroy_extrabold, FontWeight.ExtraBold),
    Font(R.font.gilroy_bold, FontWeight.Bold),
    Font(R.font.gilroy_semibold, FontWeight.SemiBold),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_regular, FontWeight.W400),
)

// Using golden ratio (φ ≈ 1.618) to scale between related text styles
val Typography = Typography(
    // Display styles - using 36sp as base
    displayLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 57.sp, // 36 * 1.618 ≈ 57
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp, // Base size for display
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline styles - using 24sp as base
    headlineLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp, // 24 * 1.33 ≈ 32
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp, // Base size for headlines
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title styles - using 16sp as base
    titleLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp, // 16 * 1.375 ≈ 22
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, // Base size for titles
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body styles - using 16sp as base
    bodyLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp, // Base size for body
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp, // 16 / 1.14 ≈ 14
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp, // 16 / 1.33 ≈ 12
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label styles - using 14sp as base
    labelLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp, // Base size for labels
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, // 14 / 1.16 ≈ 12
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp, // 14 / 1.27 ≈ 11
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)