package fr.optivision.argentica.ui.activity.photo.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.databinding.ItemSpinnerBinding

class SpinnerAdapter(var list: ArrayList<String>): RecyclerView.Adapter<SpinnerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : ItemSpinnerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.itemText.text = this
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}