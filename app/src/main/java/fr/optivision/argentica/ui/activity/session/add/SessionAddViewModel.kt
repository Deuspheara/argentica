package fr.optivision.argentica.ui.activity.session.add

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.*
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef
import kotlinx.coroutines.flow.Flow

class SessionAddViewModel : ViewModel() {

    private val address = MutableLiveData<Address>()
    private val photos = MutableLiveData<List<WitnessPhoto>>()

    fun addPhotos(photoList: List<WitnessPhoto>) {
        val list = photos.value?.toMutableList() ?: mutableListOf()
        list.addAll(photoList)
        photos.postValue(list)
    }

    fun getPhotos(): LiveData<List<WitnessPhoto>> {
        return photos
    }

    fun getAddress(): MutableLiveData<Address> {
        return address
    }

    fun setAddress(address: Address) {
        this.address.postValue(address)
    }

    suspend fun getOrCreatePlaceDatabase(place: Place): Long {
        return ArgenticaDatabase.getInstance().placeDao().getPlaceByName(place.name) ?: ArgenticaDatabase.getInstance().placeDao().insertPlace(place)
    }

    suspend fun insertSessionDatabase(session: Session): Long {
        return ArgenticaDatabase.getInstance().sessionDao().insertSession(session)
    }

    suspend fun insertWitnessPhotoDatabase(sessionId: Long) {
        photos.value?.let { list ->
            for (witnessPhoto in list) {
                witnessPhoto.sessionId = sessionId
            }
            ArgenticaDatabase.getInstance().witnessPhotoDao().insertAllWitnessPhotos(list)
        }
    }

    fun getSessionById(id: Long): Flow<SessionObjects> {
        return ArgenticaDatabase.getInstance().sessionDao().getSessionObjectBySessionId(id)
    }

    suspend fun updatePlace(place: Place) {
        ArgenticaDatabase.getInstance().placeDao().updatePlace(place)
    }

    suspend fun updateSession(session: Session) {
        ArgenticaDatabase.getInstance().sessionDao().updateSession(session)
    }

    suspend fun deleteWitnessPhotoDatabase(witnessPhotos: List<WitnessPhoto>) {
        ArgenticaDatabase.getInstance().witnessPhotoDao().deleteWitnessPhotos(witnessPhotos)
    }

    fun getCategoryById(id: Long): Flow<Category> {
        return ArgenticaDatabase.getInstance().categoryDao().getCategoryById(id)
    }

    suspend fun insertCategorySession(list: List<SessionCategoryCrossRef>) {
        ArgenticaDatabase.getInstance().sessionCategoryCrossRefDao().insertAllSessionCategoryCrossRefs(list)
    }

}