package fr.optivision.argentica.ui.activity.main

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import fr.optivision.argentica.R
import fr.optivision.argentica.data.database.ArgenticaDatabase
import fr.optivision.argentica.databinding.ActivityMainBinding
import fr.optivision.argentica.ui.activity.login.LoginActivity
import fr.optivision.argentica.ui.fragments.home.HomeViewModel
import fr.optivision.argentica.utils.RoomAddData
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnDrawer: DrawerLayout
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeViewModel.getTitle().observe(this) {
            binding.titleMain.text = it
        }

        prefs = getSharedPreferences("fr.optivision.argentica", MODE_PRIVATE);

        btnDrawer = binding.drawerLayout

        binding.btnDrawer.setOnClickListener {
            if (btnDrawer.isOpen) {
                btnDrawer.closeDrawer(GravityCompat.START)
            } else {
                btnDrawer.openDrawer(GravityCompat.START)
            }
        }

        val navHeader = binding.nvView.getHeaderView(0)
        navHeader.close_btn.setOnClickListener {
            btnDrawer.closeDrawer(GravityCompat.START)
        }

        navController = Navigation.findNavController(this, R.id.fragment)
        setupWithNavController(binding.nvView, navController)

        binding.nvView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_logout) {
                Toast.makeText(applicationContext, "Vous êtes déconnecté !", Toast.LENGTH_SHORT).show()
                //disconnect user from firebase
                FirebaseAuth.getInstance().signOut()
                //disconnect user from google
                if(GoogleSignIn.getLastSignedInAccount(this) != null){
                    GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                }
            }
            onNavDestinationSelected(menuItem, navController)

            binding.titleMain.text = menuItem.title

            btnDrawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).apply()
            lifecycleScope.launch(Dispatchers.IO) {
                ArgenticaDatabase.getInstance().clearAllTables()
                RoomAddData.addData()
            }
        }

        //lauch account fragment
        binding.accountButton.setOnClickListener {
           val intent = android.content.Intent(this, LoginActivity::class.java)
           startActivity(intent)
        }
    }
}