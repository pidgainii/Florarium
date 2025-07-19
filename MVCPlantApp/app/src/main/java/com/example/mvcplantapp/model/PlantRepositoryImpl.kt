package com.example.mvcplantapp.model

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

object PlantRepositoryImpl : PlantRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private const val SUPABASE_URL = "url"
    private const val SUPABASE_KEY = "key"
    private const val BUCKET_NAME = "florarium"

    private val supabase = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Storage)
    }

    override suspend fun getAllPlants(): List<PlantModel> {
        val plantsList = mutableListOf<PlantModel>()

        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val plantsCollection = db.collection("users").document(userId).collection("plants")

            val querySnapshot = plantsCollection.get().await()
            for (document in querySnapshot.documents) {
                val plant = document.toObject(PlantModel::class.java)
                if (plant != null) {
                    plantsList.add(plant)
                }
            }
        } catch (e: Exception) {
            println("Error getting plants: ${e.message}")
        }

        return plantsList
    }

    override suspend fun addPlant(plant: PlantModel, uri: Uri, context: Context): Boolean {
        val byteArray = uriToByteArray(uri, context)
        val nonNullableByteArray: ByteArray = byteArray ?: ByteArray(0)

        val bucket = supabase.storage.from(BUCKET_NAME)

        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val id = plant.id

            bucket.upload("$id.png", nonNullableByteArray) {
                upsert = false
            }

            val imageUrl = "/storage/v1/object/public/$BUCKET_NAME/$id.png"

            val updatedPlant = plant.copy(imageUrl = imageUrl)

            db.collection("users")
                .document(userId)
                .collection("plants")
                .document(updatedPlant.id)
                .set(updatedPlant, SetOptions.merge())
                .await()

            true
        } catch (e: Exception) {
            println("Error getting plants: ${e.message}")
            false
        }
    }

    override suspend fun updatePlant(plant: PlantModel): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val plantDocument = db.collection("users")
                .document(userId)
                .collection("plants")
                .document(plant.id)

            val snapshot = plantDocument.get().await()
            if (snapshot.exists()) {
                val updates = mapOf(
                    "name" to plant.name,
                    "description" to plant.description
                )

                plantDocument.set(updates, SetOptions.merge()).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun deletePlant(id: String) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            db.collection("users")
                .document(userId)
                .collection("plants")
                .document(id)
                .delete()
                .await()
        } catch (e: Exception) {
        }
    }

    private fun uriToByteArray(uri: Uri, context: Context): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
}
