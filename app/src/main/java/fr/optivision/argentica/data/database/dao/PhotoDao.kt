package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPhoto(photo: Photo): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAllPhotos(photos: List<Photo>): List<Long>

    @Query("SELECT * FROM Photo")
    fun getAllPhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM Photo WHERE photoId = :id")
    fun getPhotoById(id: Long): Flow<Photo>

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)
}