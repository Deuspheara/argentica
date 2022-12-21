package fr.optivision.argentica.ui.activity.viewall

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(private val itemLayoutId: Int, var items: List<T>) : RecyclerView.Adapter<GenericViewHolder>() {

    abstract fun bindData(holder: GenericViewHolder, item: T)

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        return GenericViewHolder(parent, itemLayoutId)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        bindData(holder, items[position])
    }

    abstract fun refresh(list: List<T>)
}

class GenericViewHolder(parent: ViewGroup, layoutId: Int) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))