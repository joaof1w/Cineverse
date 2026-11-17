package com.equipe.cineverse.userinterface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.equipe.cineverse.data.Personagem
import com.equipe.cineverse.viewmodel.CineViewModelFactory
import com.equipe.cineverse.viewmodel.PersonagensViewModel
import com.equipe.cineverse.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonagensScreen(
    aoAbrirPersonagem: (Int) -> Unit,
    viewModel: PersonagensViewModel = viewModel(factory = CineViewModelFactory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val busca by viewModel.busca.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current

    Scaffold(topBar = { TopAppBar(title = { Text("Personagens") }) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            OutlinedTextField(
                value = busca,
                onValueChange = viewModel::aoMudarBusca,
                placeholder = { Text("Buscar por nome") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (busca.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.aoMudarBusca("")
                            viewModel.carregar()
                        }) { Icon(Icons.Filled.Clear, contentDescription = "Limpar busca") }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    foco.clearFocus()
                    viewModel.carregar()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (val e = estado) {
                UiState.Carregando -> CarregandoView()
                is UiState.Erro -> ErroView(e.mensagem, aoTentarNovamente = viewModel::carregar)
                is UiState.Sucesso -> {
                    val lista = e.dados
                    if (lista.itens.isEmpty()) {
                        MensagemVazia("Nenhum personagem encontrado com esse nome.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(lista.itens, key = { it.id }) { personagem ->
                                PersonagemItem(personagem, aoClicar = { aoAbrirPersonagem(personagem.id) })
                            }
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
    }
}

@Composable
fun PersonagemItem(personagem: Personagem, aoClicar: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = aoClicar)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = personagem.imagem,
                contentDescription = personagem.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(personagem.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PontoDeStatus(personagem.status)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${traduzirStatus(personagem.status)} • ${personagem.especie}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RodapeCarregarMais(carregando: Boolean, erro: String?, aoCarregarMais: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
        if (carregando) {
            CircularProgressIndicator()
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                erro?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
                OutlinedButton(onClick = aoCarregarMais) { Text("Carregar mais") }
            }
        }
    }
}
