package fr.optivision.argentica.ui.activity.viewall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import fr.optivision.argentica.R
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.FilmObjects
import fr.optivision.argentica.data.model.SessionObjects
import fr.optivision.argentica.databinding.ActivityViewAllBinding
import fr.optivision.argentica.extension.toPx
import fr.optivision.argentica.ui.activity.category.CategoryActivity
import fr.optivision.argentica.ui.activity.film.show.FilmShowActivity
import fr.optivision.argentica.ui.activity.session.show.SessionShowActivity
import kotlinx.android.synthetic.main.adapter_item_category.view.*
import kotlinx.android.synthetic.main.adapter_item_film.view.*
import kotlinx.android.synthetic.main.adapter_item_film.view.constraintLayoutFilm
import kotlinx.android.synthetic.main.adapter_item_session.view.*
import kotlinx.android.synthetic.main.adapter_item_session.view.cardViewDetail
import kotlinx.coroutines.launch

class ViewAllActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewAllViewModel
    private lateinit var binding: ActivityViewAllBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ViewAllViewModel::class.java]

        intent.getStringExtra("type")?.let { type ->
            binding.title.text = type

            when (type) {
                "Séances" -> setupSessionView()
                "Categories" -> setupCategoryView()
                "Pellicules" -> setupFilmView()
            }
        }

        binding.close.setOnClickListener {
            binding.searchBar.text.clear()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    fun getLayoutChildParams(height: Int = -1): GridLayout.LayoutParams {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels
        return GridLayout.LayoutParams(
            ViewGroup.LayoutParams(
                ((dpWidth / 2.0) - 24.toPx).toInt(),
                if (height != -1) height else (dpWidth / 2) + 4.toPx.toInt()
            )
        )
    }

    fun getLayoutParentParams(): GridLayout.LayoutParams {
        val layoutParams = GridLayout.LayoutParams(ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
        layoutParams.setMargins(16.toPx.toInt(), 0, 0, 16.toPx.toInt())
        return layoutParams
    }

    private fun setupSessionView() {
        var sessionObjects = emptyList<SessionObjects>()
        val adapter = createSessionAdapter(sessionObjects)
        binding.viewAllRecyclerview.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.viewAllRecyclerview.adapter = adapter

        lifecycleScope.launch {
            viewModel.getSessions().collect { list ->
                adapter.refresh(list)
                sessionObjects = list
            }
        }

        binding.searchBar.doOnTextChanged { charSequences, _, _, _ ->
            val text = charSequences.toString().lowercase()
            if (text.isNotBlank()) {
                val list = sessionObjects.filter {
                    it.session.title.lowercase().contains(text) || it.session.description.lowercase().contains(text) || it.session.schedule.lowercase()
                        .contains(text) || it.place?.name?.lowercase()?.contains(text) == true
                }
                adapter.refresh(list)
            } else {
                adapter.refresh(sessionObjects)
            }
        }
    }

    private fun setupCategoryView() {
        var categories = emptyList<Category>()
        val adapter = createCategoryAdapter(categories)
        binding.viewAllRecyclerview.layoutManager = GridLayoutManager(applicationContext, 3)
        binding.viewAllRecyclerview.adapter = adapter

        lifecycleScope.launch {
            viewModel.getCategories().collect { list ->
                adapter.refresh(list)
                categories = list
            }
        }

        binding.searchBar.doOnTextChanged { charSequences, _, _, _ ->
            val text = charSequences.toString().lowercase()
            if (text.isNotBlank()) {
                val list = categories.filter {
                    it.name.lowercase().contains(text)
                }
                adapter.refresh(list)
            } else {
                adapter.refresh(categories)
            }
        }
    }

    private fun setupFilmView() {
        var filmsObjects = emptyList<FilmObjects>()
        val adapter = createFilmAdapter(filmsObjects)
        binding.viewAllRecyclerview.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.viewAllRecyclerview.adapter = adapter

        lifecycleScope.launch {
            viewModel.getFilms().collect { list ->
                adapter.refresh(list)
                filmsObjects = list
            }
        }

        binding.searchBar.doOnTextChanged { charSequences, _, _, _ ->
            val text = charSequences.toString().lowercase()
            if (text.isNotBlank()) {
                val list = filmsObjects.filter {
                    it.film.name.lowercase().contains(text) || it.film.brand.lowercase().contains(text) || it.film.iso.lowercase().contains(text)
                }
                adapter.refresh(list)
            } else {
                adapter.refresh(filmsObjects)
            }
        }
    }

    private fun createSessionAdapter(sessions: List<SessionObjects>): BaseAdapter<SessionObjects> {
        return object : BaseAdapter<SessionObjects>(R.layout.adapter_item_session, sessions) {
            override fun bindData(holder: GenericViewHolder, item: SessionObjects) {
                holder.itemView.hourTextView.text = item.session.schedule
                holder.itemView.placeTextView.text = item.place?.name
                holder.itemView.descriptionTextView.text = item.session.description
                holder.itemView.titleTextView.text = item.session.title
                holder.itemView.titleTextView.isSelected = true

                holder.itemView.cardViewDetail.layoutParams = getLayoutChildParams()
                holder.itemView.constraintLayoutSession.layoutParams = getLayoutParentParams()

                holder.itemView.cardViewDetail.setOnClickListener {
                    val intent = Intent(applicationContext, SessionShowActivity::class.java)
                    intent.putExtra("sessionId", item.session.id)
                    startActivity(intent)
                }
            }

            override fun refresh(list: List<SessionObjects>) {
                this.items = list
                this.notifyDataSetChanged()
            }
        }
    }

    private fun createCategoryAdapter(categories: List<Category>): BaseAdapter<Category> {
        return object : BaseAdapter<Category>(R.layout.adapter_item_category, categories) {
            override fun bindData(holder: GenericViewHolder, item: Category) {
                holder.itemView.category.text = item.name
                holder.itemView.card_category.setOnClickListener {
                    val intent = Intent(applicationContext, CategoryActivity::class.java)
                    intent.putExtra("categoryId", item.id)
                    startActivity(intent)
                }
            }

            override fun refresh(list: List<Category>) {
                this.items = list
                this.notifyDataSetChanged()
            }
        }
    }

    private fun createFilmAdapter(films: List<FilmObjects>): BaseAdapter<FilmObjects> {
        return object : BaseAdapter<FilmObjects>(R.layout.adapter_item_film, films) {
            override fun bindData(holder: GenericViewHolder, item: FilmObjects) {

                holder.itemView.cardViewDetail.layoutParams = getLayoutChildParams(180.toPx.toInt())
                holder.itemView.constraintLayoutFilm.layoutParams = getLayoutParentParams()

                holder.itemView.filmName.text = item.film.name
                holder.itemView.film_brand.text = item.film.brand
                holder.itemView.film_color.text = item.film.color
                holder.itemView.film_iso.text = item.film.iso
                holder.itemView.film_size.text = item.film.size.toString()

                holder.itemView.cardViewDetail.setOnClickListener {
                    val intent = Intent(applicationContext, FilmShowActivity::class.java)
                    intent.putExtra("filmId", item.film.id)
                    startActivity(intent)
                }
            }

            override fun refresh(list: List<FilmObjects>) {
                this.items = list
                this.notifyDataSetChanged()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            binding.searchBar.isFocusable = false
            binding.searchBar.isFocusableInTouchMode = true
        }
        return super.dispatchTouchEvent(ev)
    }
}