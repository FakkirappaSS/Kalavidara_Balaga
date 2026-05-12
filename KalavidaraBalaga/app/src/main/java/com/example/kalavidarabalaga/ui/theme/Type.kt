package com.example.kalavidarabalaga.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.example.kalavidarabalaga.R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Noto Sans Kannada")

val NotoSansKannada = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = NotoSansKannada,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansKannada,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansKannada,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansKannada,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansKannada,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)