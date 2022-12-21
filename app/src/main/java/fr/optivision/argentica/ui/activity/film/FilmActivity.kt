package fr.optivision.argentica.ui.activity.film

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import fr.optivision.argentica.R
import fr.optivision.argentica.data.model.Film
import fr.optivision.argentica.databinding.ActivityFilmBinding
import fr.optivision.argentica.interfaces.OnSnapPositionChangeListener
import fr.optivision.argentica.ui.activity.photo.add.SpinnerAdapter
import fr.optivision.argentica.utils.SnapOnScrollListener
import kotlinx.coroutines.launch

class FilmActivity : AppCompatActivity(), OnSnapPositionChangeListener {

    private lateinit var binding: ActivityFilmBinding
    private lateinit var filmViewModel: FilmViewModel
    private val isoList = arrayListOf("25", "50", "100", "200", "400", "800", "1600", "3200", "6400", "12800", "25600")
    private val cameraList: ArrayList<String> = arrayListOf()

    companion object {
        const val TYPE_ISO = "iso"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filmViewModel = ViewModelProvider(this)[FilmViewModel::class.java]

        val filmId = intent.getLongExtra("filmId", -1L)

        val isoAdapter = SpinnerAdapter(isoList)
        binding.isoRv.adapter = isoAdapter
        binding.isoRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val isoSnapHelper = LinearSnapHelper()
        isoSnapHelper.attachToRecyclerView(binding.isoRv)
        val isoSnapOnScrollListener = SnapOnScrollListener(isoSnapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, this, TYPE_ISO)
        binding.isoRv.addOnScrollListener(isoSnapOnScrollListener)

        val displayMetrics = resources.displayMetrics
        val pixels: Float = displayMetrics.density * 40f
        val padding = displayMetrics.widthPixels / 2 - pixels.toInt()
        binding.isoRv.setPadding(padding, 0, padding, 0)

        filmViewModel.cameraList.observe(this) {
            for (item in it) {
                cameraList.add(item.name)
            }
            val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner_value, cameraList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCamera.adapter = adapter
        }

        filmViewModel.getCameraList()

        binding.saveButton.setOnClickListener {
            if (filmViewModel.cameraList.value?.size == 0) {
                Toast.makeText(this, "Vous devez spécifier une camera", Toast.LENGTH_SHORT).show()
            }
            else if (binding.sizeEd.text.isBlank()) {
                Toast.makeText(this, "Vous devez spécifier une taille", Toast.LENGTH_SHORT).show()
            }
            else {
                if (filmId != -1L) {
                    filmViewModel.film.value?.id?.let { it1 ->
                        getFilmInfo(
                            it1
                        )
                    }?.let { it2 -> filmViewModel.updateFilm(it2) }
                }
                else {
                    filmViewModel.addFilm(getFilmInfo())
                }
                finish()
            }
        }

        filmViewModel.film.observe(this) {
            setFilmInfo(it)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        if (filmId != -1L) {
            binding.title.text = "Modifier la pellicule"
            filmViewModel.getFilm(filmId)
        }
    }

    override fun onSnapPositionChange(position: Int, type: String) {
        binding.isoValue.text = isoList[position]
    }

    private fun getFilmInfo(id: Long = -1): Film {
        return if (id != -1L) {
            Film(
                id = id,
                cameraId = filmViewModel.cameraList.value?.get(binding.spinnerCamera.selectedItemPosition)?.id ?: -1,
                brand = binding.brandEd.text.toString(),
                name = binding.nameEd.text.toString(),
                type = binding.typeEd.text.toString(),
                format = binding.formatEd.text.toString(),
                color = binding.colorEd.text.toString(),
                iso = binding.isoValue.text.toString(),
                size = binding.sizeEd.text.toString().toInt()
            )
        } else {
            Film(
                cameraId = filmViewModel.cameraList.value?.get(binding.spinnerCamera.selectedItemPosition)?.id ?: -1,
                brand = binding.brandEd.text.toString(),
                name = binding.nameEd.text.toString(),
                type = binding.typeEd.text.toString(),
                format = binding.formatEd.text.toString(),
                color = binding.colorEd.text.toString(),
                iso = binding.isoValue.text.toString(),
                size = binding.sizeEd.text.toString().toInt()
            )
        }
    }

    private fun setFilmInfo(film: Film) {
        binding.brandEd.setText(film.brand)
        binding.typeEd.setText(film.type)
        binding.nameEd.setText(film.name)
        binding.sizeEd.setText(film.size.toString())
        binding.colorEd.setText(film.color)
        binding.formatEd.setText(film.format)
        binding.isoValue.text = film.iso
        binding.isoRv.scrollToPosition(isoList.indexOf(film.iso))
        lifecycleScope.launch {
            filmViewModel.getCamera(film.cameraId).collect {
                binding.spinnerCamera.setSelection(cameraList.indexOf(it.name))
            }
        }
    }
}