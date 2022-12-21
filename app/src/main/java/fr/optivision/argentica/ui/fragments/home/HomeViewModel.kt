package fr.optivision.argentica.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.FilmObjects
import fr.optivision.argentica.data.model.SessionObjects
import kotlinx.coroutines.flow.Flow

class HomeViewModel : ViewModel() {

    //livedata title
    private var title : MutableLiveData<String> = MutableLiveData()

    fun getTopCategories(number: Int): Flow<List<Category>> {
        return ArgenticaDatabase.getInstance().categoryDao().getTopCategories(number)
    }

    fun getSessions(): Flow<List<SessionObjects>> {
        return ArgenticaDatabase.getInstance().sessionDao().getAllSessionObjectsWithoutUnclassified()
    }

    fun getFilms(): Flow<List<FilmObjects>> {
        return ArgenticaDatabase.getInstance().filmDao().getAllFilmObjects()
    }

    fun getTitle() : LiveData<String> {
        return title
    }

    fun setTitle(title: String) {
        this.title.postValue(title)
    }
}