package fr.optivision.argentica.ui.fragments.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.Camera
import fr.optivision.argentica.databinding.AdapterItemCamerasBinding
import fr.optivision.argentica.utils.ImageDialog

class CamerasAdapter(
    private val onClick: (Bundle) -> Unit, private val onDelete: (Camera) -> Unit
) : RecyclerView.Adapter<CamerasAdapter.CameraViewHolder>() {

    private var camerasList: List<Camera> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val binding = AdapterItemCamerasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CameraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        holder.bind(camerasList[position])
    }

    override fun getItemCount(): Int {
        return camerasList.size
    }

    override fun getItemId(position: Int): Long {
        return camerasList[position].id
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addCameras(cameras: List<Camera>) {
        camerasList = cameras
        notifyDataSetChanged()
    }

    inner class CameraViewHolder(private val binding: AdapterItemCamerasBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:Camera) {
            binding.cameraName.text = item.name
            binding.imageShowCamera.visibility = View.INVISIBLE

            binding.cameraName.isSelected = true
            val lensBundle = bundleOf(
                "titleFrom" to "show",
                "cameraId" to item.id,
                "cameraName" to item.name
            )

            binding.imageShowCamera.setOnLongClickListener {
                binding.overlayDeleteCamera.isVisible = true
                true
            }

            binding.deleteIconCamera.setOnClickListener {
                onDelete(item)
            }

            binding.imageShowCamera.setOnClickListener {
                if (binding.overlayDeleteCamera.isVisible) binding.overlayDeleteCamera.isVisible = false
                else onClick(lensBundle)
            }

            binding.cameraName.setOnClickListener {
                if (binding.overlayDeleteCamera.isVisible) binding.overlayDeleteCamera.isVisible = false
                else onClick(lensBundle)
            }

            if (item.uri.isNotEmpty()) {
                ImageDialog.decodeBase64ToBitmap(item.uri) {
                    binding.imageShowCamera.setImageBitmap(it)
                }
                binding.logoCamera.isVisible = false
            } else {
                binding.logoCamera.isVisible = true
                binding.imageShowCamera.setImageResource(0)
            }
            binding.imageShowCamera.visibility = View.VISIBLE
        }
    }
}