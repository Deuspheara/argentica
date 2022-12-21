package fr.optivision.argentica.ui.fragments.lens.show

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.Lens
import fr.optivision.argentica.databinding.AdapterItemLensBinding
import fr.optivision.argentica.utils.ImageDialog

class LensAdapter(
    private val onClick: (Bundle) -> Unit, private val onDelete: (Lens) -> Unit
) : RecyclerView.Adapter<LensAdapter.LensViewHolder>() {

    private var lens: List<Lens> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LensViewHolder {
        val binding = AdapterItemLensBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LensViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LensViewHolder, position: Int) {
        holder.bind(lens[position])
    }

    override fun getItemCount(): Int {
        return lens.size
    }

    override fun getItemId(position: Int): Long {
        return lens[position].id
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addLens(lenses: List<Lens>) {
        lens = lenses
        notifyDataSetChanged()
    }

    inner class LensViewHolder(private val binding: AdapterItemLensBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lens) {
            binding.lensName.text = item.name
            binding.imageShowLens.visibility = INVISIBLE

            binding.lensName.isSelected = true
            val lensBundle = bundleOf(
                "titleFrom" to "show",
                "lensId" to item.id,
                "lensName" to item.name
            )

            binding.imageShowLens.setOnLongClickListener {
                binding.overlayDelete.isVisible = true
                true
            }

            binding.deleteIcon.setOnClickListener {
                onDelete(item)
            }

            binding.lensName.setOnClickListener {
                if (binding.overlayDelete.isVisible) binding.overlayDelete.isVisible = false
                else onClick(lensBundle)
            }

            binding.imageShowLens.setOnClickListener {
                if (binding.overlayDelete.isVisible) binding.overlayDelete.isVisible = false
                else onClick(lensBundle)
            }

            if (item.uri.isNotEmpty()) {
                ImageDialog.decodeBase64ToBitmap(item.uri) { bitmap ->
                    binding.imageShowLens.setImageBitmap(bitmap)
                }
                binding.logoLens.isVisible = false
            } else {
                binding.logoLens.isVisible = true
                binding.imageShowLens.setImageResource(0)
            }
            binding.imageShowLens.visibility = VISIBLE
        }
    }
}