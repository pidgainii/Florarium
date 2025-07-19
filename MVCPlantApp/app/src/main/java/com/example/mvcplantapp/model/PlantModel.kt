package com.example.mvcplantapp.model

data class PlantModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    var imageUrl: String = "" // Usaremos la URL de la imagen almacenada en Supabase
)
