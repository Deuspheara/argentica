package fr.optivision.argentica.ui.activity.session.add

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import fr.optivision.argentica.R
import fr.optivision.argentica.data.model.*
import fr.optivision.argentica.data.model.association.SessionCategoryCrossRef
import fr.optivision.argentica.databinding.ActivitySessionAddBinding
import fr.optivision.argentica.ui.activity.photo.add.SpinnerAdapter
import fr.optivision.argentica.utils.ImageDialog
import fr.optivision.argentica.utils.ImageDialog.fileUriToBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SessionAddActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var sessionId: Long = -1
    private var cal = Calendar.getInstance()
    private lateinit var lastSessionObject: SessionObjects
    private lateinit var binding: ActivitySessionAddBinding
    private lateinit var sessionAddAdapter: SessionAddAdapter
    private lateinit var sessionAddViewModel: SessionAddViewModel
    private val categoriesArray: ArrayList<Category> = arrayListOf()
    private lateinit var adapterCategories: SpinnerAdapter

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
        binding = ActivitySessionAddBinding.inflate(layoutInflater)
        sessionAddViewModel = ViewModelProvider(this)[SessionAddViewModel::class.java]

        sessionId = intent.getLongExtra("session", -1)

        adapterCategories = SpinnerAdapter(arrayListOf())
        binding.categoriesRecyclerview.adapter = adapterCategories
        binding.categoriesRecyclerview.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        if (sessionId != -1L) {
            lifecycleScope.launch {
                sessionAddViewModel.getSessionById(sessionId).collect {
                    sessionAddViewModel.addPhotos(it.witnessPhoto)
                    binding.editTextSessionTitle.setText(it.session.title)
                    binding.editTextSessionPlace.setText(it.place?.name ?: "")
                    binding.editTextSessionDescription.setText(it.session.description)
                    binding.editTextSessionHour.text = it.session.schedule
                    categoriesArray.addAll(it.categories)
                    fillAdapter()
                    lastSessionObject = it
                }
            }
        }

        getLocation()
        if (sessionId == -1L) updateDateInView()

        binding.checkSessionBtn.setOnClickListener {
            createSession()
        }

        binding.backSessionBtn.setOnClickListener {
            finish()
        }

        binding.addSessionBtn.setOnClickListener {
            ImageDialog.takePicture(startForProfileImageResult, this)
        }

        binding.addCategory.setOnClickListener {
            CategoryModal {
                if (it.size > 0) {
                    categoriesArray.clear()
                    adapterCategories.list.clear()
                    adapterCategories.notifyDataSetChanged()
                    var index = 0
                    for(cat in it) {
                        lifecycleScope.launch {
                            sessionAddViewModel.getCategoryById(cat).collect {
                                if (!categoriesArray.contains(it)) {
                                    categoriesArray.add(it)
                                    adapterCategories.list.add(it.name)
                                    adapterCategories.notifyItemInserted(index)
                                    index++
                                }
                            }
                        }
                    }

                }
            }.show(supportFragmentManager, "ModalCategory")
        }

        sessionAddAdapter = SessionAddAdapter()

        binding.viewPagerAddPhoto.apply {
            adapter = sessionAddAdapter
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        }
        setupTransformer()
        setContentView(binding.root)

        sessionAddViewModel.getPhotos().observe(this) {
            if (it.isNotEmpty()) binding.imageVP.visibility = GONE
            else binding.imageVP.visibility = INVISIBLE
            sessionAddAdapter.refresh(it)
        }

        sessionAddViewModel.getAddress().observe(this) {
            binding.editTextSessionTitle.hint = it.locality + " " + SimpleDateFormat("MM/yy", Locale.getDefault()).format(Calendar.getInstance().time)
            binding.editTextSessionPlace.hint = it.getAddressLine(0)
        }

        binding.editTextSessionHour.setOnClickListener {
            DatePickerDialog(this, R.style.datepicker, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun fillAdapter() {
        for (cat in categoriesArray) {
            adapterCategories.list.add(cat.name)
        }
        adapterCategories.notifyDataSetChanged()
    }

    private fun createSession() {
        lifecycleScope.launch(Dispatchers.IO) {
            var placeName = binding.editTextSessionPlace.text.toString()
            var sessionTitle = binding.editTextSessionTitle.text.toString()

            if (placeName.isBlank()) {
                if (binding.editTextSessionPlace.hint.isBlank()) {
                    Toast.makeText(this@SessionAddActivity, "Veuillez rentrer un lieu", Toast.LENGTH_SHORT).show()
                    return@launch
                } else {
                    placeName = binding.editTextSessionPlace.hint.toString()
                }
            }

            if (sessionTitle.isBlank()) {
                if (binding.editTextSessionTitle.hint.isBlank()) {
                    Toast.makeText(this@SessionAddActivity, "Veuillez rentrer un titre", Toast.LENGTH_SHORT).show()
                    return@launch
                } else {
                    sessionTitle = binding.editTextSessionTitle.hint.toString()
                }
            }

            if (sessionId == -1L) {
                val placeId = sessionAddViewModel.getOrCreatePlaceDatabase(
                    Place(
                        name = placeName,
                        longitude = 0.0,
                        latitude = 0.0
                    )
                )
                val sessionId = sessionAddViewModel.insertSessionDatabase(
                    Session(
                        title = sessionTitle,
                        placeId = placeId,
                        schedule = binding.editTextSessionHour.text.toString(),
                        description = binding.editTextSessionDescription.text.toString()
                    )
                )
                sessionAddViewModel.insertWitnessPhotoDatabase(sessionId)
                val tmpArray: ArrayList<SessionCategoryCrossRef> = arrayListOf()
                for (cat in categoriesArray) {
                    tmpArray.add(SessionCategoryCrossRef(sessionId, cat.id))
                }
                sessionAddViewModel.insertCategorySession(tmpArray)
            } else {
                sessionAddViewModel.updatePlace(
                    Place(
                        id = lastSessionObject.session.placeId,
                        name = placeName,
                        longitude = 0.0,
                        latitude = 0.0
                    )
                )
                sessionAddViewModel.updateSession(
                    Session(
                        id = lastSessionObject.session.id,
                        title = sessionTitle,
                        placeId = lastSessionObject.session.placeId,
                        schedule = binding.editTextSessionHour.text.toString(),
                        description = binding.editTextSessionDescription.text.toString()
                    )
                )
                sessionAddViewModel.deleteWitnessPhotoDatabase(lastSessionObject.witnessPhoto)
                sessionAddViewModel.insertWitnessPhotoDatabase(sessionId)
                val tmpArray: ArrayList<SessionCategoryCrossRef> = arrayListOf()
                for (cat in categoriesArray) {
                    tmpArray.add(SessionCategoryCrossRef(lastSessionObject.session.id, cat.id))
                }
                sessionAddViewModel.insertCategorySession(tmpArray)
            }

            finish()
        }
    }

    private fun setupTransformer() {
        binding.viewPagerAddPhoto.setPageTransformer { page, position ->
            val myOffset = position * -(2 * 20 + 20)
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.translationX = 0f
            } else if (position <= 1) { // [-1,1]
                page.translationX = myOffset
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.translationX = 0f
            }
        }
    }

    private fun addPhoto(path: String) {
        sessionAddViewModel.addPhotos(listOf(WitnessPhoto(uri = path)))
    }

    private fun getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 22)
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            val test = fusedLocationClient.lastLocation
            test.addOnSuccessListener { location ->
                if (location != null) {
                    Geocoder(this, Locale.getDefault()).getAddress(location.latitude, location.longitude) { address: Address? ->
                        address?.let {
                            sessionAddViewModel.setAddress(address)
                        }
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    fun Geocoder.getAddress(latitude: Double, longitude: Double, address: (Address?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
            return
        }

        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch (e: Exception) {
            address(null)
        }
    }

    private fun updateDateInView() {
        binding.editTextSessionHour.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 22) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getLocation()
        }
    }

    override fun onDateSet(dp: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }
}