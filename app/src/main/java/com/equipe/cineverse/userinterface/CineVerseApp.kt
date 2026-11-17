package com.equipe.cineverse.userinterface

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.equipe.cineverse.viewmodel.AuthViewModel
import com.equipe.cineverse.viewmodel.CineViewModelFactory

@Composable
fun CineVerseApp(
    authViewModel: AuthViewModel = viewModel(factory = CineViewModelFactory)
) {
    val navController = rememberNavController()

    // Se já existe sessão salva, o app abre direto nas abas; senão, no login.
    val inicio: Rota = remember { if (authViewModel.estaLogado) Rota.Personagens else Rota.Login }

    val entradaAtual by navController.currentBackStackEntryAsState()
    val destinoAtual = entradaAtual?.destination
    val mostrarBarra = abas.any { destinoAtual?.hasRoute(it.rota::class) == true }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    abas.forEach { aba ->
                        val selecionada = destinoAtual?.hierarchy?.any { it.hasRoute(aba.rota::class) } == true
                        NavigationBarItem(
                            selected = selecionada,
                            onClick = { navController.irParaAba(aba.rota) },
                            icon = { Icon(aba.icone, contentDescription = null) },
                            label = { Text(aba.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = inicio,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            composable<Rota.Login> {
                LoginScreen(
                    viewModel = authViewModel,
                    aoEntrar = { navController.limparPilhaENavegar(Rota.Personagens) },
                    aoCriarConta = {
                        authViewModel.limparEstado()
                        navController.navigate(Rota.Cadastro)
                    }
                )
            }
            composable<Rota.Cadastro> {
                CadastroScreen(
                    viewModel = authViewModel,
                    aoCadastrar = { navController.popBackStack() },
                    aoVoltar = {
                        authViewModel.limparEstado()
                        navController.popBackStack()
                    }
                )
            }
            composable<Rota.Personagens> {
                PersonagensScreen(aoAbrirPersonagem = { id -> navController.navigate(Rota.DetalhePersonagem(id)) })
            }
            composable<Rota.Episodios> {
                EpisodiosScreen()
            }
            composable<Rota.Favoritos> {
                FavoritosScreen(aoAbrirPersonagem = { id -> navController.navigate(Rota.DetalhePersonagem(id)) })
            }
            composable<Rota.Perfil> {
                PerfilScreen(
                    authViewModel = authViewModel,
                    aoSair = {
                        authViewModel.sair()
                        navController.limparPilhaENavegar(Rota.Login)
                    }
                )
            }
            composable<Rota.DetalhePersonagem> { entrada ->
                val rota = entrada.toRoute<Rota.DetalhePersonagem>()
                DetalheScreen(personagemId = rota.id, aoVoltar = { navController.popBackStack() })
            }
        }
    }
}

// Troca de aba sem empilhar telas repetidas, guardando o estado de cada aba.
private fun NavHostController.irParaAba(rota: Rota) {
    navigate(rota) {
        popUpTo(Rota.Personagens) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Usado no login e no Sair: ninguém volta para a tela anterior com o botão Voltar.
private fun NavHostController.limparPilhaENavegar(rota: Rota) {
    navigate(rota) {
        popUpTo(graph.id) { inclusive = true }
    }
}
