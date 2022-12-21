package fr.optivision.argentica.ui.activity.session.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.WitnessPhoto
import fr.optivision.argentica.databinding.AdapterItemWitnessPhotoBinding
import fr.optivision.argentica.utils.ImageDialog

class ViewPagerAdapter(private var witnessPhotos: List<WitnessPhoto>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = AdapterItemWitnessPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(witnessPhotos[position])
    }

    override fun getItemCount(): Int {
        return witnessPhotos.size
    }

    fun refresh(list: List<WitnessPhoto>) {
        this.witnessPhotos = list
        notifyDataSetChanged()
    }

    class ViewPagerViewHolder(private val binding: AdapterItemWitnessPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(witnessPhoto: WitnessPhoto) {
            ImageDialog.decodeBase64ToBitmap(witnessPhoto.uri) {
                binding.witnessPhoto.setImageBitmap(it)
            }
        }
    }
}