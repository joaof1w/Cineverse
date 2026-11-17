package com.equipe.cineverse.data

import android.content.Context
import com.equipe.cineverse.network.RetrofitInstance

// Cria (uma única vez) o banco, a sessão e os repositórios que o app usa.
class AppContainer private constructor(context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val sessao = SessaoManager(context)

    val authRepository = AuthRepository(database.usuarioDao(), sessao)
    val cineRepository = CineRepository(RetrofitInstance.api, database.favoritoDao(), sessao)

    companion object {
        @Volatile
        private var INSTANCIA: AppContainer? = null

        fun get(context: Context): AppContainer =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: AppContainer(context.applicationContext).also { INSTANCIA = it }
            }
    }
}
