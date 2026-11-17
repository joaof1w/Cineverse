package com.equipe.cineverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipe.cineverse.data.CineRepository
import com.equipe.cineverse.data.Personagem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class DetalheViewModel(private val repository: CineRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Personagem>>(UiState.Carregando)
    val estado: StateFlow<UiState<Personagem>> = _estado.asStateFlow()

    private val personagemId = MutableStateFlow<Int?>(null)

    val ehFavorito: StateFlow<Boolean> = personagemId
        .filterNotNull()
        .flatMapLatest { repository.ehFavorito(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun carregar(id: Int, forcar: Boolean = false) {
        if (!forcar && personagemId.value == id && _estado.value is UiState.Sucesso) return
        personagemId.value = id
        viewModelScope.launch {
            _estado.value = UiState.Carregando
            try {
                _estado.value = UiState.Sucesso(repository.buscarPersonagem(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Erro(e.mensagemAmigavel())
            }
        }
    }

    fun alternarFavorito() {
        val personagem = (_estado.value as? UiState.Sucesso)?.dados ?: return
        viewModelScope.launch {
            if (ehFavorito.value) repository.removerFavorito(personagem.id)
            else repository.salvarFavorito(personagem)
        }
    }
}
