package com.equipe.cineverse.userinterface

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipe.cineverse.data.Episodio
import com.equipe.cineverse.viewmodel.CineViewModelFactory
import com.equipe.cineverse.viewmodel.EpisodiosViewModel
import com.equipe.cineverse.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodiosScreen(
    viewModel: EpisodiosViewModel = viewModel(factory = CineViewModelFactory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Episódios") }) }) { inner ->
        val modifier = Modifier.padding(inner)
        when (val e = estado) {
            UiState.Carregando -> CarregandoView(modifier)
            is UiState.Erro -> ErroView(e.mensagem, aoTentarNovamente = viewModel::carregar, modifier = modifier)
            is UiState.Sucesso -> {
                val lista = e.dados
                LazyColumn(
                    modifier = modifier,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista.itens, key = { it.id }) { EpisodioItem(it) }
                    if (lista.temMais) {
                        item {
                            RodapeCarregarMais(
                                carregando = lista.carregandoMais,
                                erro = lista.erroAoCarregarMais,
                                aoCarregarMais = viewModel::carregarMais
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodioItem(episodio: Episodio) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                episodio.codigo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(episodio.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Exibido em ${episodio.dataExibicao} • ${episodio.personagens.size} personagens",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
