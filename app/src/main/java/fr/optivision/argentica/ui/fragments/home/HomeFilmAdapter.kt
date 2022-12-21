package fr.optivision.argentica.ui.fragments.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.FilmObjects
import fr.optivision.argentica.databinding.AdapterItemFilmBinding

class HomeFilmAdapter(private val onClickListener: (FilmObjects) -> Unit) : RecyclerView.Adapter<HomeFilmAdapter.HomeFilmViewHolder>() {

    private var filmList: List<FilmObjects> = listOf()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): HomeFilmViewHolder {
        val binding = AdapterItemFilmBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return HomeFilmViewHolder(binding.root, onClickListener)
    }

    override fun onBindViewHolder(holder: HomeFilmViewHolder, position: Int) {
        holder.bind(filmList[position])
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun addFilms(films: List<FilmObjects>) {
        filmList = films
        notifyDataSetChanged()
    }

    class HomeFilmViewHolder(itemView: View, private val onClickListener: (FilmObjects) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(filmObjects: FilmObjects) {
            val binding = AdapterItemFilmBinding.bind(itemView)

            binding.filmName.text = filmObjects.film.name
            binding.filmBrand.text = filmObjects.film.brand
            binding.filmColor.text = filmObjects.film.color
            binding.filmIso.text = filmObjects.film.iso
            binding.filmSize.text = filmObjects.film.size.toString()

            binding.cardViewDetail.setOnClickListener {
                onClickListener(filmObjects)
            }
        }
    }
}