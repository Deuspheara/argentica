package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Camera
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCamera(camera: Camera): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCameras(cameras: List<Camera>): List<Long>

    @Query("SELECT * FROM Camera")
    fun getAllCameras(): Flow<List<Camera>>

    @Query("SELECT * FROM Camera WHERE cameraId = :id")
    fun getCameraById(id: Long): Flow<Camera>

    @Update
    suspend fun updateCamera(camera: Camera)

    @Delete
    suspend fun deleteCamera(camera: Camera)
}