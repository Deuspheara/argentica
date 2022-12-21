package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.WitnessPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface WitnessPhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWitnessPhoto(witnessPhoto: WitnessPhoto): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllWitnessPhotos(witnessPhotos: List<WitnessPhoto>): List<Long>

    @Query("SELECT * FROM WitnessPhoto")
    fun getAllWitnessPhotos(): Flow<List<WitnessPhoto>>

    @Query("SELECT * FROM WitnessPhoto WHERE witnessPhotoId = :id")
    fun getWitnessPhotoById(id: Long): Flow<WitnessPhoto>

    @Update
    suspend fun updateWitnessPhoto(witnessPhoto: WitnessPhoto)

    @Delete
    suspend fun deleteWitnessPhoto(witnessPhoto: WitnessPhoto)

    @Delete
    suspend fun deleteWitnessPhotos(witnessPhotos: List<WitnessPhoto>)
}