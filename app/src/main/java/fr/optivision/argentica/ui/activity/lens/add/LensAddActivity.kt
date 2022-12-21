package fr.optivision.argentica.ui.activity.lens.add

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import fr.optivision.argentica.data.model.Lens
import fr.optivision.argentica.databinding.ActivityLensAddBinding
import fr.optivision.argentica.ui.fragments.lens.show.LensViewModel
import fr.optivision.argentica.utils.ImageDialog
import fr.optivision.argentica.utils.ImageDialog.fileUriToBase64
import kotlinx.coroutines.launch

class LensAddActivity : AppCompatActivity() {

    private var titleFrom = ""
    private var lens: Lens? = null
    private lateinit var binding: ActivityLensAddBinding
    private lateinit var lensAddViewModel: LensViewModel

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { fileUri ->
                fileUriToBase64(
                    fileUri = fileUri,
                    onImageConverted = ::addPhoto
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lensAddViewModel = ViewModelProvider(this)[LensViewModel::class.java]
        binding = ActivityLensAddBinding.inflate(layoutInflater)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            titleFrom = bundle.getString("titleFrom", "")
            val lensId = bundle.getLong("lensId", -1L)

            if (lensId != -1L) {
                lifecycleScope.launch {
                    lensAddViewModel.getLens(lensId).collect {
                        lens = it
                        setupView(it)
                    }
                }
            }

            binding.addLensBtn.setOnClickListener {
                ImageDialog.takePicture(startForProfileImageResult, this)
            }

            lensAddViewModel.getPhotos().observe(this) {
                ImageDialog.decodeBase64ToBitmap(it) { bitmap ->
                    binding.imageLens.setImageBitmap(bitmap)
                }
            }

            binding.backLensBtn.setOnClickListener {
                saveLens()
            }
        }

        setContentView(binding.root)
    }

    private fun setupView(lens: Lens) {
        lensAddViewModel.addPhoto(lens.uri)

        binding.editTextNameLens.setText(lens.name)

        //title of fragment
        when (titleFrom) {
            "add" -> binding.nameLens.text = "Ajouter un objectif"
            "show" -> binding.nameLens.text = "Objectif"
            else -> binding.nameLens.text = ""
        }

        //image of Lens
        if (lens.uri.isNotBlank()) {
            ImageDialog.decodeBase64ToBitmap(lens.uri) {
                binding.imageLens.setImageBitmap(it)
            }
        }
    }

    override fun onBackPressed() {
        saveLens()
    }

    private fun addPhoto(path: String) {
        lensAddViewModel.addPhoto(path)
    }

    fun saveLens() {
        val newLens = Lens(
            lens?.id ?: 0L,
            binding.editTextNameLens.text.toString(),
            lensAddViewModel.getPhotos().value ?: ""
        )

        if (newLens.name.isBlank() && newLens.uri.isBlank() || lens?.id == -1L) {
            finish()
            return
        }

        if (binding.editTextNameLens.text.isBlank()) {
            newLens.name = lens?.name ?: ""
        }

        when (titleFrom) {
            "add" -> {
                lifecycleScope.launch {
                    lensAddViewModel.addLens(newLens)
                }
            }
            "show" -> {
                lifecycleScope.launch {
                    lensAddViewModel.updateLens(newLens)
                }
            }
        }
        finish()
    }
}