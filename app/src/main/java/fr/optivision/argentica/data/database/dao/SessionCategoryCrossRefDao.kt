package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionCategoryCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSessionCategoryCrossRef(sessionCategoryCrossRef: SessionCategoryCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSessionCategoryCrossRefs(sessionCategoryCrossRefs: List<SessionCategoryCrossRef>): List<Long>

    @Query("SELECT * FROM SessionCategoryCrossRef")
    fun getAllSessionCategoryCrossRefs(): Flow<List<SessionCategoryCrossRef>>

    @Update
    suspend fun updateSessionCategoryCrossRef(sessionCategoryCrossRefs: SessionCategoryCrossRef)

    @Delete
    suspend fun deleteSessionCategoryCrossRef(sessionCategoryCrossRefs: SessionCategoryCrossRef)
}