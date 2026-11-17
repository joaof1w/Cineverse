package com.equipe.cineverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipe.cineverse.data.AuthRepository
import com.equipe.cineverse.data.ResultadoAuth
import com.equipe.cineverse.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthFormState(
    val carregando: Boolean = false,
    val erro: String? = null,
    val mensagem: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(AuthFormState())
    val estado: StateFlow<AuthFormState> = _estado.asStateFlow()

    val estaLogado: Boolean
        get() = repository.usuarioIdLogado.value != null

    val usuario: StateFlow<Usuario?> = repository.usuarioLogado
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun entrar(email: String, senha: String, aoEntrar: () -> Unit) {
        viewModelScope.launch {
            _estado.value = AuthFormState(carregando = true)
            when (val resultado = repository.entrar(email, senha)) {
                is ResultadoAuth.Sucesso -> {
                    _estado.value = AuthFormState()
                    aoEntrar()
                }
                is ResultadoAuth.Falha -> _estado.value = AuthFormState(erro = resultado.mensagem)
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String, confirmacao: String, aoCadastrar: () -> Unit) {
        viewModelScope.launch {
            _estado.value = AuthFormState(carregando = true)
            when (val resultado = repository.cadastrar(nome, email, senha, confirmacao)) {
                is ResultadoAuth.Sucesso -> {
                    _estado.value = AuthFormState(mensagem = "Conta criada! Agora é só entrar.")
                    aoCadastrar()
                }
                is ResultadoAuth.Falha -> _estado.value = AuthFormState(erro = resultado.mensagem)
            }
        }
    }

    fun limparEstado() {
        _estado.value = AuthFormState()
    }

    fun sair() {
        repository.sair()
        limparEstado()
    }
}
