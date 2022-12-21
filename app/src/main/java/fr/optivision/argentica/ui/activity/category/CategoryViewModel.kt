package fr.optivision.argentica.ui.activity.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.CategoryWithSessions
import fr.optivision.argentica.data.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {

    private val _categoryId = MutableLiveData<Long>()
    val categoryId: LiveData<Long> = _categoryId
    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    fun getCategoryAndSessions(id: Long): Flow<CategoryWithSessions> {
        return ArgenticaDatabase.getInstance().categoryDao().getCategoryByIdWithSessions(id)
    }

    fun getPlace(id: Long): Flow<Place> {
        return ArgenticaDatabase.getInstance().placeDao().getPlaceById(id)
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            ArgenticaDatabase.getInstance().categoryDao().updateCategory(category)
        }
    }

    fun getCategories(): Flow<List<Category>> {
        return ArgenticaDatabase.getInstance().categoryDao().getAllCategories()
    }

    fun insertCategories(category: Category) {
        viewModelScope.launch {
            _categoryId.value = ArgenticaDatabase.getInstance().categoryDao().insertCategory(category)
        }
    }

    suspend fun getCategoryById(id: Long) {
        ArgenticaDatabase.getInstance().categoryDao().getCategoryById(id).collect {_category.value = it}
    }

}