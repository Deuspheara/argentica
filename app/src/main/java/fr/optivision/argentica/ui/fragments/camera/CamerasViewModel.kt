package fr.optivision.argentica.ui.fragments.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Camera
import kotlinx.coroutines.flow.Flow

class CamerasViewModel : ViewModel() {

    private val photos = MutableLiveData<String>()

    fun addPhoto(photo: String) {
        photos.postValue(photo)
    }

    suspend fun updateCamera(camera: Camera) {
        ArgenticaDatabase.getInstance().cameraDao().updateCamera(camera)
    }

    fun getPhotos(): LiveData<String> {
        return photos
    }

    suspend fun deleteCamera(camera: Camera) {
        ArgenticaDatabase.getInstance().cameraDao().deleteCamera(camera)
    }

    fun getCameras(): Flow<List<Camera>> {
        return ArgenticaDatabase.getInstance().cameraDao().getAllCameras()
    }

    suspend fun addCamera(camera: Camera) {
        ArgenticaDatabase.getInstance().cameraDao().insertCamera(camera)
    }

    fun getCameraById(cameraId: Long): Flow<Camera> {
        return ArgenticaDatabase.getInstance().cameraDao().getCameraById(cameraId)
    }
}