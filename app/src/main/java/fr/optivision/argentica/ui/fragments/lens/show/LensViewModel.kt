package fr.optivision.argentica.ui.fragments.lens.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.Lens
import kotlinx.coroutines.flow.Flow

class LensViewModel : ViewModel() {

    private val photos = MutableLiveData<String>()

    fun addPhoto(photo: String) {
        photos.postValue(photo)
    }

    suspend fun updateLens(lens: Lens) {
        ArgenticaDatabase.getInstance().lensDao().updateLens(lens)
    }

    fun getPhotos(): LiveData<String> {
        return photos
    }

    fun getLenses(): Flow<List<Lens>> {
        return ArgenticaDatabase.getInstance().lensDao().getAllLens()
    }

    fun getLens(id: Long): Flow<Lens> {
        return ArgenticaDatabase.getInstance().lensDao().getLensById(id)
    }

    suspend fun addLens(lens: Lens) {
        ArgenticaDatabase.getInstance().lensDao().insertLens(lens)
    }

    suspend fun deleteAllLens(lens: List<Lens>) {
        ArgenticaDatabase.getInstance().lensDao().deleteLenses(lens)
    }

    suspend fun deleteLens(lens: Lens) {
        ArgenticaDatabase.getInstance().lensDao().deleteLens(lens)
    }
}