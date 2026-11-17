package com.equipe.cineverse.viewmodel

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

// Os três estados que toda tela que busca dados da API precisa mostrar.
sealed interface UiState<out T> {
    data object Carregando : UiState<Nothing>
    data class Sucesso<T>(val dados: T) : UiState<T>
    data class Erro(val mensagem: String) : UiState<Nothing>
}

fun Throwable.mensagemAmigavel(): String = when (this) {
    is IOException -> "Sem conexão com a internet. Verifique sua rede e tente novamente."
    is HttpException -> "O servidor respondeu com erro ${code()}. Tente novamente mais tarde."
    is SerializationException -> "Os dados recebidos vieram em um formato inesperado."
    else -> "Algo deu errado: ${message ?: "erro desconhecido"}"
}
