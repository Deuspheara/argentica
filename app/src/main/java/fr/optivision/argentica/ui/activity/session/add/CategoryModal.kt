package fr.optivision.argentica.ui.activity.session.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import fr.optivision.argentica.R
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.databinding.ModalCategoryBinding
import fr.optivision.argentica.ui.activity.category.CategoryViewModel
import kotlinx.coroutines.launch

class CategoryModal(val onAdd: (ArrayList<Long>) -> Unit): DialogFragment() {

    private lateinit var binding: ModalCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private val listOfSelected: ArrayList<Long> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
        binding = ModalCategoryBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        val adapter = CategoriesModalAdapter(requireContext()) {
            if (listOfSelected.contains(it))
                listOfSelected.remove(it)
            else
                listOfSelected.add(it)
        }
        binding.categories.adapter = adapter
        binding.categories.layoutManager = GridLayoutManager(context, 3)
        lifecycleScope.launch {
            viewModel.getCategories().collect {
                adapter.refresh(it)
            }
        }
        viewModel.categoryId.observe(this) {
            lifecycleScope.launch {
                viewModel.getCategories().collect {
                    adapter.refresh(it)
                }
            }
        }
        binding.addCategory.setOnClickListener {
            if (binding.addName.text.isNotBlank()) {
                viewModel.insertCategories(Category(name = binding.addName.text.toString()))
            }
        }
        binding.validate.setOnClickListener {
            onAdd(listOfSelected)
            dismiss()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
        dialog!!.window?.setLayout(width, height)
    }
}