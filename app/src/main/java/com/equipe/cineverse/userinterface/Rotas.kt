package com.equipe.cineverse.userinterface

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Rotas type-safe: cada tela é uma classe/objeto serializável.
@Serializable
sealed interface Rota {
    @Serializable data object Login : Rota
    @Serializable data object Cadastro : Rota
    @Serializable data object Personagens : Rota
    @Serializable data object Episodios : Rota
    @Serializable data object Favoritos : Rota
    @Serializable data object Perfil : Rota

    // A tela de detalhe recebe o id do personagem escolhido.
    @Serializable data class DetalhePersonagem(val id: Int) : Rota
}

data class Aba(val rota: Rota, val titulo: String, val icone: ImageVector)

val abas = listOf(
    Aba(Rota.Personagens, "Personagens", Icons.Filled.Person),
    Aba(Rota.Episodios, "Episódios", Icons.Filled.PlayArrow),
    Aba(Rota.Favoritos, "Favoritos", Icons.Filled.Favorite),
    Aba(Rota.Perfil, "Perfil", Icons.Filled.AccountCircle)
)
