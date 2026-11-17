package com.equipe.cineverse.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Cada favorito pertence a um usuário: a chave é (usuarioId, personagemId).
// Assim dois usuários podem favoritar o mesmo personagem sem se misturar.
@Entity(
    tableName = "favoritos",
    primaryKeys = ["usuarioId", "personagemId"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class Favorito(
    val usuarioId: Long,
    val personagemId: Int,
    val nome: String,
    val imagem: String,
    val especie: String,
    val status: String,
    val salvoEm: Long = System.currentTimeMillis()
)
