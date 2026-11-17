package com.equipe.cineverse.network

import com.equipe.cineverse.data.EpisodiosResposta
import com.equipe.cineverse.data.Personagem
import com.equipe.cineverse.data.PersonagensResposta
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun listarPersonagens(
        @Query("page") pagina: Int,
        @Query("name") nome: String? = null
    ): PersonagensResposta

    @GET("character/{id}")
    suspend fun buscarPersonagem(@Path("id") id: Int): Personagem

    @GET("episode")
    suspend fun listarEpisodios(@Query("page") pagina: Int): EpisodiosResposta
}
