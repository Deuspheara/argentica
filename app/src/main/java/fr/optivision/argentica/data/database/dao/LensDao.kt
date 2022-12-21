package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Lens
import kotlinx.coroutines.flow.Flow

@Dao
interface LensDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLens(lens: Lens): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllLens(lens: List<Lens>): List<Long>

    @Query("SELECT * FROM Lens")
    fun getAllLens(): Flow<List<Lens>>

    @Query("SELECT * FROM Lens WHERE lensId = :id")
    fun getLensById(id: Long): Flow<Lens>

    @Update
    suspend fun updateLens(lens: Lens)

    @Delete
    suspend fun deleteLens(lens: Lens)

    @Delete
    suspend fun deleteLenses(lenses: List<Lens>)
}