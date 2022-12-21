package fr.optivision.argentica.ui.activity.film.show

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.enum.AdapterType
import fr.optivision.argentica.data.enum.ShootingMode
import fr.optivision.argentica.data.model.FilmObject
import fr.optivision.argentica.databinding.AdapterItemHeaderBinding
import fr.optivision.argentica.databinding.AdapterItemPhotoBinding
import kotlinx.android.synthetic.main.adapter_item_header.view.*
import kotlinx.android.synthetic.main.adapter_item_photo.view.*

class FilmShowAdapter(
    private val onLongClick: (Int) -> Unit,
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<FilmShowAdapter.LensShowViewHolder>() {

    private var listFilm: List<FilmObject> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LensShowViewHolder {
        return if (viewType == AdapterType.LINEAR.ordinal) {
            val binding = AdapterItemHeaderBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
            LensShowViewHolder(binding.root)
        } else {
            val binding = AdapterItemPhotoBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
            LensShowViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(holder: LensShowViewHolder, position: Int) {
        holder.bind(listFilm[position], position)
    }

    override fun getItemCount(): Int {
        return listFilm.size
    }

    fun addFilmObjects(filmObjects: List<FilmObject>) {
        listFilm = filmObjects
        notifyDataSetChanged()
    }

    //change view type
    override fun getItemViewType(position: Int): Int {
        return if (listFilm[position].adapterType == AdapterType.LINEAR) {
            AdapterType.LINEAR.ordinal
        } else {
            AdapterType.GRID.ordinal
        }
    }

    inner class LensShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(filmObject: FilmObject, position: Int) {
            if (filmObject.adapterType == AdapterType.LINEAR) {
                itemView.headerText.text = filmObject.session?.title
            } else {
                itemView.photo_number.text = filmObject.photo?.number.toString()
                itemView.photo_aperture.text = filmObject.photo?.aperture.toString()
                itemView.photo_exposure_time.text = filmObject.photo?.exposureTime.toString()
                itemView.photo_shooting_mode.text = when (filmObject.photo?.shootingMode) {
                    ShootingMode.MANUAL.name -> "M"
                    ShootingMode.PROGRAM.name -> "P"
                    ShootingMode.SHUTTER_PRIORITY.name -> "S"
                    ShootingMode.APERTURE_PRIORITY.name -> "A"
                    else -> "NO MODE"
                }

                itemView.photoMenu.isVisible = filmObject.isVisible

                itemView.clPhoto.setOnLongClickListener {
                    onLongClick(position)
                    true
                }

                itemView.photo_edit.setOnClickListener {
                    onEditClick(position)
                }

                itemView.photo_delete.setOnClickListener {
                    onDeleteClick(position)
                }
            }
        }
    }
}