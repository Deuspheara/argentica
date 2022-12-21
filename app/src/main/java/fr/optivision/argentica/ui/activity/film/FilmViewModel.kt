package fr.optivision.argentica.ui.activity.film

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Camera
import fr.optivision.argentica.data.model.Film
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FilmViewModel : ViewModel() {

    private val _cameraList = MutableLiveData<List<Camera>>()
    val cameraList: LiveData<List<Camera>> = _cameraList
    private val _film = MutableLiveData<Film>()
    val film: LiveData<Film> = _film


    fun getCameraList() {
        viewModelScope.launch {
            val camera = ArgenticaDatabase.getInstance().cameraDao().getAllCameras()
            camera.collect {
                _cameraList.value = it
            }
        }
    }

    fun getCamera(id: Long): Flow<Camera> {
        return ArgenticaDatabase.getInstance().cameraDao().getCameraById(id)
    }

    fun getFilm(id: Long) {
        viewModelScope.launch {
            ArgenticaDatabase.getInstance().filmDao().getFilmById(id).collect { _film.value = it }
        }
    }

    fun addFilm(film: Film) {
        viewModelScope.launch {
            ArgenticaDatabase.getInstance().filmDao().insertFilm(film)
        }
    }

    fun updateFilm(film: Film) {
        viewModelScope.launch {
            ArgenticaDatabase.getInstance().filmDao().updateFilm(film)
        }
    }

}