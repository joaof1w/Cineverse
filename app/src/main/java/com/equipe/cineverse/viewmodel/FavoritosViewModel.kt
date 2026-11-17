package com.equipe.cineverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipe.cineverse.data.CineRepository
import com.equipe.cineverse.data.Favorito
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel(private val repository: CineRepository) : ViewModel() {

    // O Repository já filtra pelo usuário logado; ao trocar de conta, a lista troca junto.
    val favoritos: StateFlow<List<Favorito>> = repository.favoritos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun remover(personagemId: Int) {
        viewModelScope.launch { repository.removerFavorito(personagemId) }
    }
}
