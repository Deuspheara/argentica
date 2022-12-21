package fr.optivision.argentica.ui.activity.login


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import fr.optivision.argentica.R
import fr.optivision.argentica.databinding.ActivityLoginBinding
import fr.optivision.argentica.ui.fragments.login.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        Log.d("LoginActivity", "onCreate: ")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_login) as NavHostFragment
        navController = navHostFragment.navController


        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null) {
            navController.navigate(R.id.action_loginFragment_to_accountFragment)
            binding.titleName.text = "Mon compte"
        }


        loginViewModel.getTitle().observe(this) {
            Log.d("LoginActivity", "getTitle Observer: $it")
            binding.titleName.text = it
        }


        binding.backLoginBtn.setOnClickListener {
            finish()
        }


        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }





}