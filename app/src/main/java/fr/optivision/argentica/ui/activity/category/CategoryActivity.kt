package fr.optivision.argentica.ui.activity.category

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import fr.optivision.argentica.R
import fr.optivision.argentica.data.model.Category
import fr.optivision.argentica.data.model.Session
import fr.optivision.argentica.databinding.ActivityCategoryBinding
import fr.optivision.argentica.extension.toPx
import fr.optivision.argentica.ui.activity.session.show.SessionShowActivity
import fr.optivision.argentica.ui.activity.viewall.BaseAdapter
import fr.optivision.argentica.ui.activity.viewall.GenericViewHolder
import kotlinx.android.synthetic.main.adapter_item_session.view.*
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val categoryId = intent.getLongExtra("categoryId", 0)

        setupSessionView(categoryId)
    }

    private fun setupSessionView(categoryId: Long) {
        var sessionObjects = emptyList<Session>()
        val adapter = createSessionAdapter(sessionObjects)
        binding.sessionsRv.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.sessionsRv.adapter = adapter

        lifecycleScope.launch {
            viewModel.getCategoryAndSessions(categoryId).collect { category ->
                binding.nameEd.setText(category.category.name)
                adapter.refresh(category.sessions)
                sessionObjects = category.sessions
                binding.backBtn.setOnClickListener {
                    if (binding.nameEd.text.isNotBlank()) {
                        viewModel.updateCategory(Category(category.category.id, binding.nameEd.text.toString()))
                    }
                    finish()
                }
            }
        }
    }

    private fun createSessionAdapter(sessions: List<Session>): BaseAdapter<Session> {
        return object : BaseAdapter<Session>(R.layout.adapter_item_session, sessions) {
            override fun bindData(holder: GenericViewHolder, item: Session) {
                holder.itemView.hourTextView.text = item.schedule
                lifecycleScope.launch {
                    viewModel.getPlace(item.placeId).collect {
                        holder.itemView.placeTextView.text = it.name
                    }
                }
                holder.itemView.descriptionTextView.text = item.description
                holder.itemView.titleTextView.text = item.title
                holder.itemView.titleTextView.isSelected = true
                holder.itemView.cardViewDetail.layoutParams = getLayoutChildParams()
                holder.itemView.constraintLayoutSession.layoutParams = getLayoutParentParams()
                holder.itemView.cardViewDetail.setOnClickListener {
                    val intent = Intent(applicationContext, SessionShowActivity::class.java)
                    intent.putExtra("sessionId", item.id)
                    startActivity(intent)
                }
            }

            override fun refresh(list: List<Session>) {
                this.items = list
                this.notifyDataSetChanged()
            }
        }
    }

    fun getLayoutChildParams(height: Int = -1): GridLayout.LayoutParams {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels
        return GridLayout.LayoutParams(
            ViewGroup.LayoutParams(
                ((dpWidth / 2.0) - 24.toPx).toInt(),
                if (height != -1) height else (dpWidth / 2) + 4.toPx.toInt()
            )
        )
    }

    fun getLayoutParentParams(): GridLayout.LayoutParams {
        val layoutParams = GridLayout.LayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        layoutParams.setMargins(16.toPx.toInt(), 0, 0, 16.toPx.toInt())
        return layoutParams
    }
}