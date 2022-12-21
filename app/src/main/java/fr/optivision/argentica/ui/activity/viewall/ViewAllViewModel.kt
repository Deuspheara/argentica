package fr.optivision.argentica.ui.activity.viewall

import androidx.lifecycle.ViewModel
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.FilmObjects
import fr.optivision.argentica.data.model.SessionObjects
import kotlinx.coroutines.flow.Flow

class ViewAllViewModel : ViewModel() {

    fun getCategories(): Flow<List<Category>> {
        return ArgenticaDatabase.getInstance().categoryDao().getAllCategories()
    }

    fun getSessions(): Flow<List<SessionObjects>> {
        return ArgenticaDatabase.getInstance().sessionDao().getAllSessionObjectsWithoutUnclassified()
    }

    fun getFilms(): Flow<List<FilmObjects>> {
        return ArgenticaDatabase.getInstance().filmDao().getAllFilmObjects()
    }
}