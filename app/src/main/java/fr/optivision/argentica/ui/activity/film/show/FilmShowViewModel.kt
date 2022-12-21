package fr.optivision.argentica.ui.activity.film.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.FilmObjects
import fr.optivision.argentica.data.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FilmShowViewModel : ViewModel() {

    fun getFilmById(id: Long): Flow<FilmObjects> {
        return ArgenticaDatabase.getInstance().filmDao().getFilmObjectByFilmId(id)
    }

    fun deletePhoto(photo: Photo?) {
        viewModelScope.launch {
            photo?.let {
                ArgenticaDatabase.getInstance().photoDao().deletePhoto(photo)
            }
        }
    }
}