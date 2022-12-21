package fr.optivision.argentica.ui.activity.camera.add

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import fr.optivision.argentica.data.model.Camera
import fr.optivision.argentica.databinding.ActivityCameraAddBinding
import fr.optivision.argentica.ui.fragments.camera.CamerasViewModel
import fr.optivision.argentica.utils.ImageDialog
import fr.optivision.argentica.utils.ImageDialog.fileUriToBase64
import kotlinx.coroutines.launch

class CameraAddActivity : AppCompatActivity() {

    private var titleFrom = ""
    private var camera: Camera? = null
    private lateinit var binding: ActivityCameraAddBinding
    private lateinit var cameraAddViewModel: CamerasViewModel

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
        cameraAddViewModel = ViewModelProvider(this)[CamerasViewModel::class.java]
        binding = ActivityCameraAddBinding.inflate(layoutInflater)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            titleFrom = bundle.getString("titleFrom", "")
            val cameraId = bundle.getLong("cameraId", -1L)

            if (cameraId != -1L) {
                lifecycleScope.launch {
                    cameraAddViewModel.getCameraById(cameraId).collect {
                        camera = it
                        setupView(it)
                    }
                }
            }

            binding.addCameraBtn.setOnClickListener {
                ImageDialog.takePicture(startForProfileImageResult, this)
            }

            cameraAddViewModel.getPhotos().observe(this) {
                ImageDialog.decodeBase64ToBitmap(it) { bitmap ->
                    binding.imageCamera.setImageBitmap(bitmap)
                }
            }

            binding.backCameraBtn.setOnClickListener {
                saveLens()
            }
        }

        setContentView(binding.root)
    }

    private fun setupView(camera: Camera) {
        cameraAddViewModel.addPhoto(camera.uri)

        binding.editTextNameCamera.setText(camera.name)

        //title of fragment
        when (titleFrom) {
            "add" -> binding.nameCamera.text = "Ajouter un appareil photo"
            "show" -> binding.nameCamera.text = "Appareil photo"
            else -> binding.nameCamera.text = ""
        }

        //image of Lens
        if (camera.uri.isNotBlank()) {
            ImageDialog.decodeBase64ToBitmap(camera.uri) {
                binding.imageCamera.setImageBitmap(it)
            }
        }
    }

    override fun onBackPressed() {
        saveLens()
    }

    private fun addPhoto(path: String) {
        cameraAddViewModel.addPhoto(path)
    }

    fun saveLens() {
        val newCamera = Camera(
            id = camera?.id ?: 0L,
            name = binding.editTextNameCamera.text.toString(),
            uri = cameraAddViewModel.getPhotos().value ?: "",
            weight = 0f,
            buyPrice = 0f,
            brand = "",
            type = ""
        )

        if (newCamera.name.isBlank() && newCamera.uri.isBlank() || camera?.id == -1L) {
            finish()
            return
        }

        if (binding.editTextNameCamera.text.isBlank()) {
            newCamera.name = camera?.name ?: ""
        }

        when (titleFrom) {
            "add" -> {
                lifecycleScope.launch {
                    cameraAddViewModel.addCamera(newCamera)
                }
            }
            "show" -> {
                lifecycleScope.launch {
                    cameraAddViewModel.updateCamera(newCamera)
                }
            }
        }
        finish()
    }
}
