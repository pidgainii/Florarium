package com.example.mvcplantapp.model




interface UserRepository {
    suspend fun register(email: String, password: String): Boolean
    suspend fun login(email: String, password: String): Boolean
    fun signOut(): Boolean
}
