package fr.optivision.argentica.ui.fragments.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import fr.optivision.argentica.data.model.SessionObjects
import fr.optivision.argentica.databinding.AdapterItemSessionBinding

class HomeSessionAdapter(private val onClickListener: (SessionObjects) -> Unit) : RecyclerView.Adapter<HomeSessionAdapter.HomeSceneViewHolder>() {

    private var sessionList: List<SessionObjects> = listOf()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): HomeSceneViewHolder {
        val binding = AdapterItemSessionBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return HomeSceneViewHolder(binding.root, onClickListener)
    }

    override fun onBindViewHolder(holder: HomeSceneViewHolder, position: Int) {
        holder.bind(sessionList[position])
    }

    override fun getItemCount(): Int {
        return sessionList.size
    }

    fun addSessions(session: List<SessionObjects>) {
        sessionList = session
        notifyDataSetChanged()
    }

    class HomeSceneViewHolder(itemView: View, private val onClickListener: (SessionObjects) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(session: SessionObjects) {
            val binding = AdapterItemSessionBinding.bind(itemView)
            binding.hourTextView.text = session.session.schedule
            binding.placeTextView.text = session.place?.name
            binding.descriptionTextView.text = session.session.description
            binding.titleTextView.text = session.session.title
            binding.titleTextView.isSelected = true
            binding.cardViewDetail.setOnClickListener {
                onClickListener(session)
            }
        }
    }
}