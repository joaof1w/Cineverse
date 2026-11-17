package com.equipe.cineverse.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// O índice único garante no banco que não existem dois usuários com o mesmo e-mail.
@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val senhaHash: String
)
