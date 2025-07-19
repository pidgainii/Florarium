package com.example.mvcplantapp.model

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object UserRepositoryImpl : UserRepository {
    override suspend fun register(email: String, password: String): Boolean {
        return try {
            // Intentar crear el usuario en Firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()

            // Verificar si el usuario se ha creado correctamente
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                FirebaseAuth.getInstance().signOut() // Opcional: cerrar sesión si es necesario
                false // No ha habido error
            } else {
                true // Ha habido error (usuario no creado)
            }
        } catch (e: Exception) {
            true // Ha habido error
        }
    }

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            // Intentar iniciar sesión en Firebase
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()

            // Verificar si el usuario está autenticado
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                false // No ha habido error (login exitoso)
            } else {
                true // Ha habido error (usuario no autenticado)
            }
        } catch (e: Exception) {
            true // Ha habido error
        }
    }

    override fun signOut(): Boolean {
        return try {
            FirebaseAuth.getInstance().signOut()
            true
        } catch (e: Exception) {
            false
        }
    }
}
