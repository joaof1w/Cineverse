package com.equipe.cineverse.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos WHERE usuarioId = :usuarioId ORDER BY salvoEm DESC")
    fun observarDoUsuario(usuarioId: Long): Flow<List<Favorito>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE usuarioId = :usuarioId AND personagemId = :personagemId)")
    fun observarEhFavorito(usuarioId: Long, personagemId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(favorito: Favorito)

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND personagemId = :personagemId")
    suspend fun remover(usuarioId: Long, personagemId: Int)
}
