package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlace(place: Place): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPlaces(places: List<Place>): List<Long>

    @Query("SELECT * FROM Place")
    fun getAllPlaces(): Flow<List<Place>>

    @Query("SELECT * FROM Place WHERE placeId = :id")
    fun getPlaceById(id: Long): Flow<Place>

    @Query("SELECT placeId FROM Place WHERE name like :name")
    fun getPlaceByName(name: String): Long?

    @Update
    suspend fun updatePlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)
}