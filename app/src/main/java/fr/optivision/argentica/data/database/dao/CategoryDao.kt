package fr.optivision.argentica.data.database.dao

import androidx.room.*
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.CategoryWithSessions
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCategories(categories: List<Category>): List<Long>

    @Query("SELECT * FROM Category")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE categoryId = :id")
    fun getCategoryById(id: Long): Flow<Category>

    @Query("SELECT c.* FROM SessionCategoryCrossRef as sccr JOIN Category as c on sccr.categoryId=c.categoryId GROUP BY c.categoryId, c.name ORDER BY Count(*) DESC LIMIT :number")
    fun getTopCategories(number: Int): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM Category")
    fun getCategoryWithSessions(): Flow<List<CategoryWithSessions>>

    @Transaction
    @Query("SELECT * FROM Category WHERE categoryId = :id")
    fun getCategoryByIdWithSessions(id: Long): Flow<CategoryWithSessions>

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}