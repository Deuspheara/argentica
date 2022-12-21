package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Session
import fr.optivision.argentica.data.model.SessionObjects
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSession(session: Session): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSessions(sessions: List<Session>): List<Long>

    @Query("SELECT * FROM Session")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM Session WHERE sessionId = :id")
    fun getSessionById(id: Long): Flow<Session>

    @Query("SELECT * FROM Session ORDER BY sessionId LIMIT 1")
    fun getFirstSession(): Flow<Session>

    @Transaction
    @Query("SELECT * FROM Session")
    fun getAllSessionObjects(): Flow<List<SessionObjects>>

    @Transaction
    @Query("SELECT * FROM Session WHERE title <> 'Non classé'")
    fun getAllSessionObjectsWithoutUnclassified(): Flow<List<SessionObjects>>

    @Transaction
    @Query("SELECT * FROM Session WHERE sessionId = :sessionId")
    fun getSessionObjectBySessionId(sessionId: Long): Flow<SessionObjects>

    @Update
    suspend fun updateSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)
}