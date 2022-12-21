package fr.optivision.argentica.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.FilmObjects
import fr.optivision.argentica.data.model.SessionObjects
import fr.optivision.argentica.databinding.FragmentMainBinding
import fr.optivision.argentica.ui.activity.category.CategoryActivity
import fr.optivision.argentica.ui.activity.film.FilmActivity
import fr.optivision.argentica.ui.activity.film.show.FilmShowActivity
import fr.optivision.argentica.ui.activity.photo.add.AddPhotoActivity
import fr.optivision.argentica.ui.activity.session.add.SessionAddActivity
import fr.optivision.argentica.ui.activity.session.show.SessionShowActivity
import fr.optivision.argentica.ui.activity.viewall.ViewAllActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentMainBinding
    private lateinit var sessionAdapter: HomeSessionAdapter
    private lateinit var filmAdapter: HomeFilmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionAdapter = HomeSessionAdapter(::openSession)
        filmAdapter = HomeFilmAdapter(::showFilm)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        binding.homeSessionRecyclerview.adapter = sessionAdapter
        binding.homeSessionRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.homeFilmRecyclerview.adapter = filmAdapter
        binding.homeFilmRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.sessionViewAll.setOnClickListener {
            startViewAllActivity("Séances")
        }

        binding.filmViewAll.setOnClickListener {
            startViewAllActivity("Pellicules")
        }

        binding.categoryViewAll.setOnClickListener {
            startViewAllActivity("Categories")
        }

        binding.fabPhoto.setOnClickListener {
            startActivity(Intent(requireContext(), AddPhotoActivity::class.java))
        }

        binding.fabLens.setOnClickListener {
            startActivity(Intent(requireContext(), FilmActivity::class.java))
        }

        binding.fabSession.setOnClickListener {
            startActivity(Intent(requireContext(), SessionAddActivity::class.java))
        }

        setupObserver()

        return binding.root
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            viewModel.getTopCategories(3).collect { list ->
                if (list.isNotEmpty()) {
                    binding.category1.text = list[0].name
                    binding.category1.setOnClickListener { openCategory(list[0]) }
                }
                if (list.size > 1) {
                    binding.category2.text = list[1].name
                    binding.category2.setOnClickListener { openCategory(list[1]) }
                }
                if (list.size > 2) {
                    binding.category3.text = list[2].name
                    binding.category3.setOnClickListener { openCategory(list[2]) }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getSessions().collect {
                sessionAdapter.addSessions(it)
            }
        }

        lifecycleScope.launch {
            viewModel.getFilms().collect {
                filmAdapter.addFilms(it)
            }
        }
    }

    private fun startViewAllActivity(type: String) {
        val intent = Intent(requireContext(), ViewAllActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    private fun openSession(session: SessionObjects) {
        val intent = Intent(context, SessionShowActivity::class.java)
        intent.putExtra("sessionId", session.session.id)
        startActivity(intent)
    }

    private fun openCategory(category: Category) {
        val intent = Intent(context, CategoryActivity::class.java)
        intent.putExtra("categoryId", category.id)
        startActivity(intent)
    }

    private fun showFilm(filmObjects: FilmObjects) {
        val intent = Intent(context, FilmShowActivity::class.java)
        intent.putExtra("filmId", filmObjects.film.id )
        startActivity(intent)
    }
}