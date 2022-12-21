package fr.optivision.argentica.ui.fragments.camera

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import fr.optivision.argentica.data.model.Camera
import fr.optivision.argentica.databinding.FragmentCameraListBinding
import fr.optivision.argentica.ui.activity.camera.add.CameraAddActivity
import kotlinx.coroutines.launch

class CamerasFragment : Fragment() {

    private lateinit var cameraAdapter: CamerasAdapter
    private lateinit var binding: FragmentCameraListBinding
    private lateinit var cameraViewModel: CamerasViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraViewModel = ViewModelProvider(requireActivity())[CamerasViewModel::class.java]
        cameraAdapter = CamerasAdapter(::onClick, ::deleteCamera)
        lifecycleScope.launch {
            cameraViewModel.getCameras().collect {
                cameraAdapter.addCameras(it)
            }
        }
    }

    private fun deleteCamera(camera: Camera) {
        lifecycleScope.launch {
            cameraViewModel.deleteCamera(camera)
        }
    }

    private fun onClick(bundle: Bundle) {
        val intent = Intent(requireContext(), CameraAddActivity::class.java)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCameraListBinding.inflate(layoutInflater)
        binding.rwCamera.adapter = cameraAdapter
        binding.rwCamera.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.addButton.setOnClickListener {
            onClick(
                bundleOf(
                    "titleFrom" to "add",
                    "cameraName" to "",
                    "cameraUri" to ""
                )
            )
        }
        return binding.root
    }
}