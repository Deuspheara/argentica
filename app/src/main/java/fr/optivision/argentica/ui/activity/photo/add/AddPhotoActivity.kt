package fr.optivision.argentica.ui.activity.photo.add

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import fr.optivision.argentica.R
import fr.optivision.argentica.data.enum.convertStringToShootingMode
import fr.optivision.argentica.databinding.ActivityAddPhotoBinding
import fr.optivision.argentica.interfaces.OnSnapPositionChangeListener
import fr.optivision.argentica.utils.SnapOnScrollListener
import kotlinx.coroutines.launch

class AddPhotoActivity : AppCompatActivity(), OnSnapPositionChangeListener, OnItemSelectedListener {

    private lateinit var binding: ActivityAddPhotoBinding
    private lateinit var addPhotoViewModel: AddPhotoViewModel
    private val apertureList = arrayListOf("f/1", "f/1.4", "f/2", "f/2.8", "f/4", "f/5.6", "f/8", "f/11", "f/16", "f/22", "f/32", "f/45")
    private val shutterList = arrayListOf("120", "60", "30", "15", "8", "4", "2", "1", "1/2", "1/4", "1/8", "1/15", "1/30", "1/60", "1/125", "1/250", "1/500", "1/1000", "1/2000", "1/4000", "1/8000")
    private val shootingMode: List<String> = listOf("Manuel", "Priorité à l'ouverture", "Priorité à la vitesse", "Automatique")

    companion object {
        const val TYPE_APERTURE = "aperture"
        const val TYPE_SHUTTER = "shutter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addPhotoViewModel = ViewModelProvider(this)[AddPhotoViewModel::class.java]

        val photoId = intent.getLongExtra("photoId", -1L)

        val apertureAdapter = SpinnerAdapter(apertureList)
        binding.apertureRv.adapter = apertureAdapter
        binding.apertureRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val apertureSnapHelper = LinearSnapHelper()
        apertureSnapHelper.attachToRecyclerView(binding.apertureRv)
        val apertureSnapOnScrollListener = SnapOnScrollListener(apertureSnapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, this, TYPE_APERTURE)
        binding.apertureRv.addOnScrollListener(apertureSnapOnScrollListener)

        val shutterAdapter = SpinnerAdapter(shutterList)
        binding.shutterRv.adapter = shutterAdapter
        binding.shutterRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val shutterSnapHelper = LinearSnapHelper()
        shutterSnapHelper.attachToRecyclerView(binding.shutterRv)
        val shutterSnapOnScrollListener = SnapOnScrollListener(shutterSnapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, this, TYPE_SHUTTER)
        binding.shutterRv.addOnScrollListener(shutterSnapOnScrollListener)

        val displayMetrics = resources.displayMetrics
        val pixels: Float = displayMetrics.density * 40f
        val padding = displayMetrics.widthPixels / 2 - pixels.toInt()
        binding.apertureRv.setPadding(padding, 0, padding, 0)
        binding.shutterRv.setPadding(padding, 0, padding, 0)

        loadData()

        addPhotoViewModel.lensList.observe(this) {
            val lensList: ArrayList<String> = arrayListOf()
            for (item in it) {
                lensList.add(item.name)
            }
            val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner_value, lensList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerLens.adapter = adapter
        }

        addPhotoViewModel.sessionsList.observe(this) {
            val sessionsList: ArrayList<String> = arrayListOf()
            for (item in it) {
                sessionsList.add(item.title)
            }
            val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner_value, sessionsList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSessions.adapter = adapter
        }

        addPhotoViewModel.filmsList.observe(this) {
            val filmsList: ArrayList<String> = arrayListOf()
            for (item in it) {
                filmsList.add(item.name)
            }
            binding.spinnerFilms.onItemSelectedListener = this
            val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner_value, filmsList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFilms.adapter = adapter
        }

        addPhotoViewModel.photoNumber.observe(this) {
            binding.photoNum.setText("${it + 1}")
        }

        val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner_value, shootingMode)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMode.adapter = adapter

        binding.saveButton.setOnClickListener {
            if (addPhotoViewModel.filmsList.value?.size == 0) {
                Toast.makeText(this, "Vous devez spécifier une pellicule", Toast.LENGTH_SHORT).show()
            }
            else if (binding.photoNum.text.isBlank()) {
                Toast.makeText(this, "Vous devez spécifier un numéro", Toast.LENGTH_SHORT).show()
            } else {
                if (photoId != -1L) {
                    //TODO Eliott update photo
                    finish()
                } else {
                    addPhotoViewModel.addPhoto(
                        binding.photoNum.text.toString().toInt(),
                        addPhotoViewModel.sessionsList.value?.get(binding.spinnerSessions.selectedItemPosition)?.id!!,
                        addPhotoViewModel.lensList.value?.get(binding.spinnerLens.selectedItemPosition)?.id!!,
                        addPhotoViewModel.filmsList.value?.get(binding.spinnerFilms.selectedItemPosition)?.id!!,
                        binding.apertureValue.text.toString(),
                        binding.shutterValue.text.toString(),
                        convertStringToShootingMode(binding.spinnerMode.selectedItem as String).name
                    )
                }
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        if (photoId != -1L) {
            binding.title.text = "Modifier la photo"
            lifecycleScope.launch {
                addPhotoViewModel.getPhoto(photoId).collect {
                    //TODO Eliott get values from photo
                }
            }
        }
    }

    override fun onSnapPositionChange(position: Int, type: String) {
        when (type) {
            TYPE_APERTURE -> binding.apertureValue.text = apertureList[position]
            TYPE_SHUTTER -> binding.shutterValue.text = "${shutterList[position]}s"
        }
    }

    private fun loadData() {
        addPhotoViewModel.getLensList()
        addPhotoViewModel.getSessionsList()
        addPhotoViewModel.getFilmsList()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        addPhotoViewModel.filmsList.value?.get(p2)?.id?.let { addPhotoViewModel.getPhotosNumber(it) }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}