package fr.optivision.argentica.ui.fragments.account

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import fr.optivision.argentica.databinding.FragmentAccountBinding
import fr.optivision.argentica.ui.activity.login.LoginActivity
import fr.optivision.argentica.ui.fragments.home.HomeViewModel
import fr.optivision.argentica.utils.DatabaseConverter
import java.util.*


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(requireContext())
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance("gs://argentica-ac8a0.appspot.com")
        if(auth.currentUser == null) {
           val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        setupUI((activity as LoginActivity))

        return binding.root
    }

    private fun setupUI(activity: LoginActivity) {
        binding.emailMyAccount.text = auth.currentUser?.email

        DatabaseConverter.setProgress(0)

        DatabaseConverter.getProgress().observe(viewLifecycleOwner) {
            binding.progressBarFirebase.progress = it
        }

        binding.btnUploadData.setOnClickListener {
            DatabaseConverter.backupDatabase(
                activity,
                auth,
                storage )
        }

        binding.btnRestoreData.setOnClickListener {
            DatabaseConverter.changeDatabase(
                activity,
                auth,
                storage
            ) {
                restartAllApp()
            }
        }
    }

    private fun restartAllApp() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        System.exit(0)
    }

}