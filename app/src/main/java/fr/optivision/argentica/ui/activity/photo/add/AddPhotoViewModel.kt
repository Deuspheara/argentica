package fr.optivision.argentica.ui.activity.photo.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Film
import fr.optivision.argentica.data.model.Lens
import fr.optivision.argentica.data.model.Photo
import fr.optivision.argentica.data.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AddPhotoViewModel : ViewModel() {

    private val _lensList = MutableLiveData<List<Lens>>()
    val lensList: LiveData<List<Lens>> = _lensList
    private val _sessionsList = MutableLiveData<List<Session>>()
    val sessionsList: LiveData<List<Session>> = _sessionsList
    private val _filmsList = MutableLiveData<List<Film>>()
    val filmsList: LiveData<List<Film>> = _filmsList
    private val _photoNumber = MutableLiveData<Int>()
    val photoNumber: LiveData<Int> = _photoNumber

    fun getLensList() {
        viewModelScope.launch {
            val lens = ArgenticaDatabase.getInstance().lensDao().getAllLens()
            lens.collect {
                _lensList.value = it
            }
        }
    }

    fun getSessionsList() {
        viewModelScope.launch {
            val sessions = ArgenticaDatabase.getInstance().sessionDao().getAllSessions()
            sessions.collect {
                _sessionsList.value = it
            }
        }
    }

    fun getFilmsList() {
        viewModelScope.launch {
            val films = ArgenticaDatabase.getInstance().filmDao().getAllFilms()
            films.collect {
                _filmsList.value = it
            }
        }
    }

    fun getPhotosNumber(filmId: Long) {
        viewModelScope.launch {
            val number = ArgenticaDatabase.getInstance().filmDao().getFilmObjectByFilmId(filmId)
            number.collect {
                val list = it.photos
                _photoNumber.value = list.sortedBy { it.number }.last().number
            }
        }
    }

    fun addPhoto(number: Int, sessionId: Long, lensId: Long, filmId: Long, aperture: String, exposureTime: String, shootingMode: String) {
        viewModelScope.launch {
            ArgenticaDatabase.getInstance().photoDao().addPhoto(
                Photo(
                    number = number,
                    sessionId = sessionId,
                    lensId = lensId,
                    filmId = filmId,
                    aperture = aperture,
                    exposureTime = exposureTime,
                    shootingMode = shootingMode
                )
            )
        }
    }

    fun getPhoto(photoId: Long): Flow<Photo> {
        return ArgenticaDatabase.getInstance().photoDao().getPhotoById(photoId)
    }
}