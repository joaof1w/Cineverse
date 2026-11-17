package com.equipe.cineverse.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Data classes que espelham o JSON da Rick and Morty API.
// @SerialName liga o nome do campo no JSON ao nome em português no Kotlin.

@Serializable
data class InfoPagina(
    @SerialName("count") val total: Int = 0,
    @SerialName("pages") val paginas: Int = 0,
    @SerialName("next") val proxima: String? = null,
    @SerialName("prev") val anterior: String? = null
)

@Serializable
data class PersonagensResposta(
    val info: InfoPagina,
    @SerialName("results") val resultados: List<Personagem>
)

@Serializable
data class Personagem(
    val id: Int,
    @SerialName("name") val nome: String,
    val status: String,
    @SerialName("species") val especie: String,
    @SerialName("type") val tipo: String = "",
    @SerialName("gender") val genero: String,
    @SerialName("origin") val origem: Lugar,
    @SerialName("location") val localizacao: Lugar,
    @SerialName("image") val imagem: String,
    @SerialName("episode") val episodios: List<String> = emptyList()
)

@Serializable
data class Lugar(
    @SerialName("name") val nome: String
)

@Serializable
data class EpisodiosResposta(
    val info: InfoPagina,
    @SerialName("results") val resultados: List<Episodio>
)

@Serializable
data class Episodio(
    val id: Int,
    @SerialName("name") val nome: String,
    @SerialName("air_date") val dataExibicao: String,
    @SerialName("episode") val codigo: String,
    @SerialName("characters") val personagens: List<String> = emptyList()
)
