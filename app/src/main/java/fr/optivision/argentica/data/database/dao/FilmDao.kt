package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Film
import fr.optivision.argentica.data.model.FilmObjects
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilm(film: Film): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllFilms(films: List<Film>): List<Long>

    @Query("SELECT * FROM Film")
    fun getAllFilms(): Flow<List<Film>>

    @Query("SELECT * FROM Film WHERE filmId = :id")
    fun getFilmById(id: Long): Flow<Film>

    @Transaction
    @Query("SELECT * FROM Film")
    fun getAllFilmObjects(): Flow<List<FilmObjects>>

    @Transaction
    @Query("SELECT * FROM Film WHERE filmId = :filmId")
    fun getFilmObjectByFilmId(filmId: Long): Flow<FilmObjects>

    @Update
    suspend fun updateFilm(film: Film)

    @Delete
    suspend fun deleteFilm(film: Film)
}