package com.equipe.cineverse.data

import com.equipe.cineverse.network.RickAndMortyApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import retrofit2.HttpException

// Único ponto que fala com a API (Retrofit) e com os favoritos (Room).
@OptIn(ExperimentalCoroutinesApi::class)
class CineRepository(
    private val api: RickAndMortyApi,
    private val favoritoDao: FavoritoDao,
    private val sessao: SessaoManager
) {

    // ---------- API ----------

    suspend fun listarPersonagens(pagina: Int, nome: String?): PersonagensResposta =
        withContext(Dispatchers.IO) {
            try {
                api.listarPersonagens(pagina, nome?.trim()?.takeIf { it.isNotEmpty() })
            } catch (e: HttpException) {
                // A API responde 404 quando a busca por nome não encontra nada.
                if (e.code() == 404) PersonagensResposta(InfoPagina(), emptyList()) else throw e
            }
        }

    suspend fun buscarPersonagem(id: Int): Personagem =
        withContext(Dispatchers.IO) { api.buscarPersonagem(id) }

    suspend fun listarEpisodios(pagina: Int): EpisodiosResposta =
        withContext(Dispatchers.IO) { api.listarEpisodios(pagina) }

    // ---------- Favoritos (sempre do usuário logado) ----------

    val favoritos: Flow<List<Favorito>> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else favoritoDao.observarDoUsuario(id)
    }

    fun ehFavorito(personagemId: Int): Flow<Boolean> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(false) else favoritoDao.observarEhFavorito(id, personagemId)
    }

    suspend fun salvarFavorito(personagem: Personagem) {
        val usuarioId = sessao.usuarioId.value ?: return
        favoritoDao.inserir(
            Favorito(
                usuarioId = usuarioId,
                personagemId = personagem.id,
                nome = personagem.nome,
                imagem = personagem.imagem,
                especie = personagem.especie,
                status = personagem.status
            )
        )
    }

    suspend fun removerFavorito(personagemId: Int) {
        val usuarioId = sessao.usuarioId.value ?: return
        favoritoDao.remover(usuarioId, personagemId)
    }
}
