// FontResources.kt
package com.example.mvcplantapp.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.mvcplantapp.R

// Definición de la familia Montserrat
val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal), // Fuente regular
    Font(R.font.montserrat_bold, FontWeight.Bold),     // Fuente en negrita
    Font(R.font.montserrat_italic, FontWeight.Light)  // Fuente cursiva
)
