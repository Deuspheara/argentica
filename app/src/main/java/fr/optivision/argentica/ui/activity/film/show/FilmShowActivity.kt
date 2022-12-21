package fr.optivision.argentica.ui.activity.film.show

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import fr.optivision.argentica.data.enum.AdapterType
import fr.optivision.argentica.data.model.FilmObject
import fr.optivision.argentica.databinding.ActivityLensShowBinding
import fr.optivision.argentica.ui.activity.film.FilmActivity
import fr.optivision.argentica.ui.activity.photo.add.AddPhotoActivity
import kotlinx.coroutines.launch

class FilmShowActivity : AppCompatActivity() {

    private lateinit var list: List<FilmObject>
    private lateinit var adapter: FilmShowAdapter
    private lateinit var binding: ActivityLensShowBinding
    private lateinit var filmShowViewModel: FilmShowViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLensShowBinding.inflate(layoutInflater)
        adapter = FilmShowAdapter(::onLongClick, ::onEditClick, ::onDeleteClick)
        filmShowViewModel = ViewModelProvider(this)[FilmShowViewModel::class.java]
        binding.rvLens.adapter = adapter


        val listFilmObject: ArrayList<FilmObject> = arrayListOf()
        val filmId = intent.getLongExtra("filmId", 0)

        lifecycleScope.launch {
            filmShowViewModel.getFilmById(filmId).collect { filmObject ->
                listFilmObject.clear()
                filmObject.sessions.sortedByDescending { it.id }.forEach { session ->
                    if (filmObject.photos.find { it.sessionId == session.id } != null) {
                        listFilmObject.add(
                            FilmObject(
                                adapterType = AdapterType.LINEAR,
                                session = session
                            )
                        )
                    }
                    filmObject.photos.forEach { photo ->
                        if (session.id == photo.sessionId) {
                            listFilmObject.add(
                                FilmObject(
                                    adapterType = AdapterType.GRID,
                                    photo = photo
                                )
                            )
                        }
                    }

                }
                list = listFilmObject
                adapter.addFilmObjects(listFilmObject)
            }
        }


        binding.backFilmBtn.setOnClickListener {
            finish()
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(applicationContext, FilmActivity::class.java)
            intent.putExtra("filmId", filmId)
            startActivity(intent)
        }

        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    AdapterType.LINEAR.ordinal -> 2
                    AdapterType.GRID.ordinal -> 1
                    else -> -1
                }
            }
        }

        binding.rvLens.layoutManager = layoutManager
        setContentView(binding.root)
    }

    private fun onLongClick(position: Int) {
        list.forEach {
            it.isVisible = false
        }
        list[position].isVisible = true
        adapter.addFilmObjects(list)
    }

    private fun onEditClick(position: Int) {
        list[position].isVisible = false
        adapter.addFilmObjects(list)
        list[position].photo?.let {
            val intent = Intent(this, AddPhotoActivity::class.java)
            intent.putExtra("photoId", it.id)
            startActivity(intent)
        }
    }

    private fun onDeleteClick(position: Int) {
        list[position].isVisible = false
        adapter.addFilmObjects(list)
        filmShowViewModel.deletePhoto(list[position].photo)
    }
}