package fr.optivision.argentica.ui.fragments.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private var title : MutableLiveData<String> = MutableLiveData()

    fun getTitle() : LiveData<String> {
        return title
    }

    fun setTitle(title: String) {
        this.title.postValue(title)
        Log.d("LoginViewModel", "setTitle: $title")
    }
}