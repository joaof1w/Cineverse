package com.equipe.cineverse.userinterface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.equipe.cineverse.data.Favorito
import com.equipe.cineverse.viewmodel.CineViewModelFactory
import com.equipe.cineverse.viewmodel.FavoritosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    aoAbrirPersonagem: (Int) -> Unit,
    viewModel: FavoritosViewModel = viewModel(factory = CineViewModelFactory)
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Meus favoritos") }) }) { inner ->
        if (favoritos.isEmpty()) {
            MensagemVazia(
                "Você ainda não salvou nenhum personagem.\nAbra um personagem e toque no coração.",
                Modifier.padding(inner)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(inner),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritos, key = { it.personagemId }) { favorito ->
                    FavoritoItem(
                        favorito = favorito,
                        aoClicar = { aoAbrirPersonagem(favorito.personagemId) },
                        aoRemover = { viewModel.remover(favorito.personagemId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritoItem(favorito: Favorito, aoClicar: () -> Unit, aoRemover: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = aoClicar)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = favorito.imagem,
                contentDescription = favorito.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(favorito.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PontoDeStatus(favorito.status)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${traduzirStatus(favorito.status)} • ${favorito.especie}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = aoRemover) {
                Icon(Icons.Filled.Delete, contentDescription = "Remover dos favoritos")
            }
        }
    }
}
