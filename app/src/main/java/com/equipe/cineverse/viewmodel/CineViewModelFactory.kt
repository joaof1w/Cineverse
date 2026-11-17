package com.equipe.cineverse.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.equipe.cineverse.data.AppContainer

// Ensina o Android a criar cada ViewModel com o Repository de que ele precisa.
val CineViewModelFactory = viewModelFactory {
    initializer { AuthViewModel(container().authRepository) }
    initializer { PersonagensViewModel(container().cineRepository) }
    initializer { EpisodiosViewModel(container().cineRepository) }
    initializer { DetalheViewModel(container().cineRepository) }
    initializer { FavoritosViewModel(container().cineRepository) }
}

private fun CreationExtras.container(): AppContainer =
    AppContainer.get(checkNotNull(this[APPLICATION_KEY]))
