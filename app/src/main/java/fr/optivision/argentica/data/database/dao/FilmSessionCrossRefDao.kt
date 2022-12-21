package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.association.FilmSessionCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmSessionCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilmSessionCrossRef(filmSessionCrossRef: FilmSessionCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllFilmSessionCrossRefs(filmSessionCrossRefs: List<FilmSessionCrossRef>): List<Long>

    @Query("SELECT * FROM FilmSessionCrossRef")
    fun getAllFilmSessionCrossRefs(): Flow<List<FilmSessionCrossRef>>

    @Update
    suspend fun updateFilmSessionCrossRef(filmSessionCrossRefs: FilmSessionCrossRef)

    @Delete
    suspend fun deleteFilmSessionCrossRef(filmSessionCrossRefs: FilmSessionCrossRef)
}