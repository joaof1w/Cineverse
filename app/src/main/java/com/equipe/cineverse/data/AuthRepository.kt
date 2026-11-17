package com.equipe.cineverse.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

sealed interface ResultadoAuth {
    data class Sucesso(val usuario: Usuario) : ResultadoAuth
    data class Falha(val mensagem: String) : ResultadoAuth
}

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val sessao: SessaoManager
) {

    val usuarioIdLogado: StateFlow<Long?> = sessao.usuarioId

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuarioLogado: Flow<Usuario?> = sessao.usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(null) else usuarioDao.observar(id)
    }

    suspend fun cadastrar(nome: String, email: String, senha: String, confirmacao: String): ResultadoAuth {
        val emailNormalizado = email.trim().lowercase()

        val erro = when {
            nome.isBlank() -> "Informe seu nome."
            !EMAIL_REGEX.matches(emailNormalizado) -> "Informe um e-mail válido."
            senha.length < 6 -> "A senha precisa ter 6 ou mais caracteres."
            senha != confirmacao -> "As senhas não conferem."
            usuarioDao.buscarPorEmail(emailNormalizado) != null -> "Este e-mail já está cadastrado."
            else -> null
        }
        if (erro != null) return ResultadoAuth.Falha(erro)

        val usuario = Usuario(nome = nome.trim(), email = emailNormalizado, senhaHash = senha.sha256())
        val id = usuarioDao.inserir(usuario)
        return ResultadoAuth.Sucesso(usuario.copy(id = id))
    }

    suspend fun entrar(email: String, senha: String): ResultadoAuth {
        val usuario = usuarioDao.buscarPorEmail(email.trim().lowercase())
        // Mesma mensagem para e-mail inexistente e senha errada: não revelamos qual campo errou.
        if (usuario == null || usuario.senhaHash != senha.sha256()) {
            return ResultadoAuth.Falha("E-mail ou senha inválidos.")
        }
        sessao.salvar(usuario.id)
        return ResultadoAuth.Sucesso(usuario)
    }

    fun sair() = sessao.limpar()

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
