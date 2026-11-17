package com.equipe.cineverse.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Guarda o id do usuário logado em SharedPreferences, para a sessão
// sobreviver a fechar e reabrir o app.
class SessaoManager(context: Context) {

    private val prefs = context.getSharedPreferences("sessao", Context.MODE_PRIVATE)

    private val _usuarioId = MutableStateFlow(
        prefs.getLong(CHAVE_USUARIO_ID, SEM_USUARIO).takeIf { it != SEM_USUARIO }
    )
    val usuarioId: StateFlow<Long?> = _usuarioId.asStateFlow()

    fun salvar(usuarioId: Long) {
        prefs.edit().putLong(CHAVE_USUARIO_ID, usuarioId).apply()
        _usuarioId.value = usuarioId
    }

    fun limpar() {
        prefs.edit().remove(CHAVE_USUARIO_ID).apply()
        _usuarioId.value = null
    }

    private companion object {
        const val CHAVE_USUARIO_ID = "usuario_id"
        const val SEM_USUARIO = -1L
    }
}
