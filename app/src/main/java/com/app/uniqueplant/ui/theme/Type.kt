package com.app.uniqueplant.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.uniqueplant.R

// Set of Material typography styles to start with

val GilroyFontFamily = FontFamily(
    Font(R.font.gilroy_black, FontWeight.Black),
    Font(R.font.gilroy_extrabold, FontWeight.ExtraBold),
    Font(R.font.gilroy_bold, FontWeight.Bold),
    Font(R.font.gilroy_semibold, FontWeight.SemiBold),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_regular, FontWeight.W400),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
    // Title of AppBar placed in center
    titleLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    // Category heading
    headlineMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    // Category heading large
    headlineLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    /*// Card item main text
        bodyLarge = TextStyle(
            fontFamily = GilroyFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.5.sp
        ),*/
    // Card item subtext
    bodyMedium = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Call to Action buttons text
    labelLarge = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.25.sp
    )
)