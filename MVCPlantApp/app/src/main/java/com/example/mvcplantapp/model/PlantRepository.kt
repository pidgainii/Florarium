package com.example.mvcplantapp.model

import android.content.Context
import android.net.Uri


interface PlantRepository {
    suspend fun getAllPlants(): List<PlantModel>
    suspend fun addPlant(plant: PlantModel, uri: Uri, context: Context): Boolean
    suspend fun updatePlant(plant: PlantModel): Boolean
    suspend fun deletePlant(id: String)
}
