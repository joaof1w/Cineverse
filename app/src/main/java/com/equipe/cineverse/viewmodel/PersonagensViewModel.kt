package com.equipe.cineverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipe.cineverse.data.CineRepository
import com.equipe.cineverse.data.Personagem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class ListaPersonagens(
    val itens: List<Personagem>,
    val temMais: Boolean,
    val carregandoMais: Boolean = false,
    val erroAoCarregarMais: String? = null
)

class PersonagensViewModel(private val repository: CineRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<ListaPersonagens>>(UiState.Carregando)
    val estado: StateFlow<UiState<ListaPersonagens>> = _estado.asStateFlow()

    private val _busca = MutableStateFlow("")
    val busca: StateFlow<String> = _busca.asStateFlow()

    private var paginaAtual = 1
    private var trabalho: Job? = null

    init {
        carregar()
    }

    fun aoMudarBusca(texto: String) {
        _busca.value = texto
    }

    fun carregar() {
        trabalho?.cancel()
        trabalho = viewModelScope.launch {
            _estado.value = UiState.Carregando
            try {
                val resposta = repository.listarPersonagens(pagina = 1, nome = _busca.value)
                paginaAtual = 1
                _estado.value = UiState.Sucesso(
                    ListaPersonagens(resposta.resultados, temMais = resposta.info.proxima != null)
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
                val resposta = repository.listarPersonagens(pagina = paginaAtual + 1, nome = _busca.value)
                paginaAtual++
                _estado.value = UiState.Sucesso(
                    ListaPersonagens(atual.itens + resposta.resultados, temMais = resposta.info.proxima != null)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Sucesso(atual.copy(erroAoCarregarMais = e.mensagemAmigavel()))
            }
        }
    }
}
