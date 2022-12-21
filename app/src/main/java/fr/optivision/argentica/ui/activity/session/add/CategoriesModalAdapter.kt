package fr.optivision.argentica.ui.activity.session.add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.R
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.databinding.AdapterItemCategoryBinding
import kotlinx.android.synthetic.main.adapter_item_category.view.*

class CategoriesModalAdapter(val context: Context, val onClick: (id: Long) -> Unit) : RecyclerView.Adapter<CategoriesModalAdapter.ViewHolder>() {

    val categories: ArrayList<Category> = arrayListOf()
    private val selected: ArrayList<Boolean> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun refresh(photoList: List<Category>) {
        categories.clear()
        categories.addAll(photoList)
        notifyDataSetChanged()
        while (selected.size < categories.size) {
            selected.add(false)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category, position: Int) {
            itemView.category.text = category.name
            itemView.card_category as CardView
            if (selected[position])
                itemView.card_category.setCardBackgroundColor(ContextCompat.getColor(context,R.color.fab_color))
            itemView.card_category.setOnClickListener {
                selected[position] = !selected[position]
                it as CardView
                if (selected[position])
                    it.setCardBackgroundColor(ContextCompat.getColor(context,R.color.fab_color))
                else
                    it.setCardBackgroundColor(ContextCompat.getColor(context,R.color.card_color))
                onClick(category.id)
            }
        }
    }
}