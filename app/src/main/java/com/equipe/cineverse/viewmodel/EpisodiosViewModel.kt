package com.equipe.cineverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipe.cineverse.data.CineRepository
import com.equipe.cineverse.data.Episodio
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class ListaEpisodios(
    val itens: List<Episodio>,
    val temMais: Boolean,
    val carregandoMais: Boolean = false,
    val erroAoCarregarMais: String? = null
)

class EpisodiosViewModel(private val repository: CineRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<ListaEpisodios>>(UiState.Carregando)
    val estado: StateFlow<UiState<ListaEpisodios>> = _estado.asStateFlow()

    private var paginaAtual = 1
    private var trabalho: Job? = null

    init {
        carregar()
    }

    fun carregar() {
        trabalho?.cancel()
        trabalho = viewModelScope.launch {
            _estado.value = UiState.Carregando
            try {
                val resposta = repository.listarEpisodios(pagina = 1)
                paginaAtual = 1
                _estado.value = UiState.Sucesso(
                    ListaEpisodios(resposta.resultados, temMais = resposta.info.proxima != null)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Erro(e.mensagemAmigavel())
            }
        }
    }

    fun carregarMais() {
        val atual = (_estado.value as? UiState.Sucesso)?.dados ?: return
        if (!atual.temMais || atual.carregandoMais) return

        _estado.value = UiState.Sucesso(atual.copy(carregandoMais = true, erroAoCarregarMais = null))
        trabalho = viewModelScope.launch {
            try {
                val resposta = repository.listarEpisodios(pagina = paginaAtual + 1)
                paginaAtual++
                _estado.value = UiState.Sucesso(
                    ListaEpisodios(atual.itens + resposta.resultados, temMais = resposta.info.proxima != null)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Sucesso(atual.copy(erroAoCarregarMais = e.mensagemAmigavel()))
            }
        }
    }
}
