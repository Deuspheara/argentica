package fr.optivision.argentica.ui.activity.session.show

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.data.model.SessionObjects
import fr.optivision.argentica.databinding.ActivitySessionShowBinding
import fr.optivision.argentica.ui.activity.session.add.SessionAddActivity
import kotlinx.coroutines.launch

class SessionShowActivity : AppCompatActivity() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var binding: ActivitySessionShowBinding
    private var sessionDao = ArgenticaDatabase.getInstance().sessionDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionShowBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val sessionId = intent.getLongExtra("sessionId", 0)
        initAdapter()

        lifecycleScope.launch {
            sessionDao.getSessionObjectBySessionId(sessionId).collect { sessionObject ->
                if (sessionObject.witnessPhoto.isNotEmpty()) binding.witnessPhotoDefault.visibility = INVISIBLE
                else binding.witnessPhotoDefault.visibility = VISIBLE
                viewPagerAdapter.refresh(sessionObject.witnessPhoto)
                binding.witnessPhotoIndicator.createIndicators(sessionObject.witnessPhoto.size, 0)
                setup(sessionObject)
            }
        }

        setupTransformer()
    }

    private fun setup(sessionObject: SessionObjects) {
        binding.nameSession.text = sessionObject.session.title
        binding.sessionPlace.text = sessionObject.place?.name
        binding.sessionHour.text = sessionObject.session.schedule
        binding.sessionDescription.text = sessionObject.session.description

        binding.btnBackShow.setOnClickListener {
            finish()
        }

        binding.btnEditPhoto.setOnClickListener {
            val intent = Intent(this, SessionAddActivity::class.java)
            intent.putExtra("session", sessionObject.session.id)
            startActivity(intent)
            finish()
        }
    }

    private fun initAdapter() {
        viewPagerAdapter = ViewPagerAdapter(emptyList())
        binding.viewPagerShowPhoto.adapter = viewPagerAdapter

        binding.viewPagerShowPhoto.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.witnessPhotoIndicator.animatePageSelected(position)
            }
        })
    }

    private fun setupTransformer() {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.viewPagerShowPhoto.apply {
            setPageTransformer(compositePageTransformer)
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        }
    }
}