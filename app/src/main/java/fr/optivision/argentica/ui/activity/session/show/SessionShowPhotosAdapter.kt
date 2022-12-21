package fr.optivision.argentica.ui.activity.session.show

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.Photo
import fr.optivision.argentica.databinding.AdapterItemPhotoBinding

class SessionShowPhotosAdapter(
    private var photos: List<Photo>,
    private val onClick: (List<Photo>) -> Unit
    ) : RecyclerView.Adapter<SessionShowPhotosAdapter.SessionShowPhotosViewHolder>() {

    private var listPhotosSelected: MutableList<Photo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionShowPhotosViewHolder {
        val binding = AdapterItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionShowPhotosViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: SessionShowPhotosViewHolder, position: Int) {
        holder.bind(photos[position], listPhotosSelected)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun removePhoto(photo: Photo) {
        listPhotosSelected.remove(photo)
    }

    fun removeListPhotosSelected(listPhotosSelected: List<Photo>) {
        //remove all photos selected
        this.listPhotosSelected.removeAll(listPhotosSelected)
    }

    fun refresh(photos: List<Photo>) {
        this.photos = photos
        notifyDataSetChanged()
    }

    class SessionShowPhotosViewHolder(private val binding: AdapterItemPhotoBinding, private val onClick: (List<Photo>) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo, listPhotosSelected: MutableList<Photo>) {
            binding.photoNumber.text = photo.number.toString()
            //TODO binding.photoIso.text = photo.iso
            binding.photoAperture.text = photo.aperture
            binding.photoExposureTime.text = photo.exposureTime
            binding.photoShootingMode.text = when (photo.shootingMode) {
                "PROGRAM" -> "P"
                "SHUTTER_PRIORITY" -> "S"
                "APERTURE_PRIORITY" -> "A"
                "MANUAL" -> "M"
                else -> "None"
            }
            binding.root.setOnLongClickListener {
                //add photo to listPhotosSelected if not in list
                if (!listPhotosSelected.contains(photo)) {
                    listPhotosSelected.add(photo)
                    //add opacity
                    binding.root.alpha = 0.5f
                    binding.checkPhoto.visibility = View.VISIBLE
                    binding.photoNumber.visibility = View.GONE
                }else {
                    listPhotosSelected.remove(photo)
                    //remove opacity
                    binding.root.alpha = 1f
                    binding.checkPhoto.visibility = View.GONE
                    binding.photoNumber.visibility = View.VISIBLE
                }
                onClick(listPhotosSelected)
                true
            }
            binding.root.setOnClickListener {
                if(listPhotosSelected.isNotEmpty()) {
                    //if photo is in listPhotosSelected, remove it
                    if (listPhotosSelected.contains(photo)) {
                        listPhotosSelected.remove(photo)
                        //remove opacity
                        binding.root.alpha = 1f
                        binding.checkPhoto.visibility = View.GONE
                        binding.photoNumber.visibility = View.VISIBLE
                    }else {
                        //add photo to listPhotosSelected
                        listPhotosSelected.add(photo)
                        //add opacity
                        binding.root.alpha = 0.5f
                        binding.checkPhoto.visibility = View.VISIBLE
                        binding.photoNumber.visibility = View.GONE
                    }
                }
                onClick(listPhotosSelected)
                true
            }
        }
    }
}