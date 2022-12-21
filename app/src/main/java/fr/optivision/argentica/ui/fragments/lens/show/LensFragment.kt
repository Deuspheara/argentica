package fr.optivision.argentica.ui.fragments.lens.show

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import fr.optivision.argentica.data.model.Lens
import fr.optivision.argentica.databinding.FragmentLensListBinding
import fr.optivision.argentica.ui.activity.lens.add.LensAddActivity
import kotlinx.coroutines.launch

class LensFragment : Fragment() {

    private lateinit var binding: FragmentLensListBinding
    private lateinit var lensAdapter: LensAdapter
    private lateinit var lensesViewModel: LensViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lensesViewModel = ViewModelProvider(requireActivity())[LensViewModel::class.java]
        lensAdapter = LensAdapter(::onClick, ::deleteLens)
        lifecycleScope.launch {
            lensesViewModel.getLenses().collect {
                lensAdapter.addLens(it)
            }
        }
    }

    private fun deleteLens(lens: Lens) {
        lifecycleScope.launch {
            lensesViewModel.deleteLens(lens)
        }
    }

    private fun onClick(bundle: Bundle) {
        val intent = Intent(requireContext(), LensAddActivity::class.java)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLensListBinding.inflate(layoutInflater)
        binding.rvLenses.adapter = lensAdapter
        binding.rvLenses.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.addButton.setOnClickListener {
            onClick(
                bundleOf(
                    "titleFrom" to "add",
                    "lensName" to "",
                    "lensUri" to ""
                )
            )
        }
        return binding.root
    }
}