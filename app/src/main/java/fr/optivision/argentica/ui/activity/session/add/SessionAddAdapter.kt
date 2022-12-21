package fr.optivision.argentica.ui.activity.session.add

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.WitnessPhoto
import fr.optivision.argentica.databinding.AdapterItemAddImageBinding
import fr.optivision.argentica.utils.ImageDialog
import kotlinx.android.synthetic.main.adapter_item_add_image.view.*

class SessionAddAdapter : RecyclerView.Adapter<SessionAddAdapter.SessionAddViewHolder>() {

    private var photos: List<WitnessPhoto> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionAddViewHolder {
        val binding = AdapterItemAddImageBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        binding.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return SessionAddViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SessionAddViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun refresh(photoList: List<WitnessPhoto>) {
        photos = photoList
        notifyDataSetChanged()
    }

    inner class SessionAddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(witnessPhoto: WitnessPhoto) {
            ImageDialog.decodeBase64ToBitmap(witnessPhoto.uri) {
                itemView.imageVP.setImageBitmap(it)
            }
        }
    }
}