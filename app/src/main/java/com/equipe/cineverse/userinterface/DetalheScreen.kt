package com.equipe.cineverse.userinterface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.equipe.cineverse.data.Personagem
import com.equipe.cineverse.viewmodel.CineViewModelFactory
import com.equipe.cineverse.viewmodel.DetalheViewModel
import com.equipe.cineverse.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(
    personagemId: Int,
    aoVoltar: () -> Unit,
    viewModel: DetalheViewModel = viewModel(factory = CineViewModelFactory)
) {
    LaunchedEffect(personagemId) { viewModel.carregar(personagemId) }

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val ehFavorito by viewModel.ehFavorito.collectAsStateWithLifecycle()
    val personagem = (estado as? UiState.Sucesso)?.dados

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(personagem?.nome ?: "Detalhes", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (personagem != null) {
                        IconButton(onClick = viewModel::alternarFavorito) {
                            Icon(
                                if (ehFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (ehFavorito) "Remover dos favoritos" else "Salvar nos favoritos",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { inner ->
        val modifier = Modifier.padding(inner)
        when (val e = estado) {
            UiState.Carregando -> CarregandoView(modifier)
            is UiState.Erro -> ErroView(
                e.mensagem,
                aoTentarNovamente = { viewModel.carregar(personagemId, forcar = true) },
                modifier = modifier
            )
            is UiState.Sucesso -> ConteudoDetalhe(
                personagem = e.dados,
                ehFavorito = ehFavorito,
                aoAlternarFavorito = viewModel::alternarFavorito,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ConteudoDetalhe(
    personagem: Personagem,
    ehFavorito: Boolean,
    aoAlternarFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = personagem.imagem,
            contentDescription = personagem.nome,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(Modifier.height(16.dp))
        Text(personagem.nome, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            PontoDeStatus(personagem.status)
            Spacer(Modifier.width(8.dp))
            Text(traduzirStatus(personagem.status), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(16.dp))

        LinhaInfo("Espécie", personagem.especie)
        if (personagem.tipo.isNotBlank()) LinhaInfo("Tipo", personagem.tipo)
        LinhaInfo("Gênero", traduzirGenero(personagem.genero))
        LinhaInfo("Origem", personagem.origem.nome)
        LinhaInfo("Última localização", personagem.localizacao.nome)
        LinhaInfo("Episódios", "Aparece em ${personagem.episodios.size} episódio(s)")

        Spacer(Modifier.height(24.dp))
        if (ehFavorito) {
            OutlinedButton(onClick = aoAlternarFavorito, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Remover dos favoritos")
            }
        } else {
            Button(onClick = aoAlternarFavorito, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Icon(Icons.Filled.FavoriteBorder, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salvar nos favoritos")
            }
        }
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(rotulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider()
}
