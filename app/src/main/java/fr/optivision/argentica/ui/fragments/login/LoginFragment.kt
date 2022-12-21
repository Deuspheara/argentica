package fr.optivision.argentica.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import fr.optivision.argentica.R
import fr.optivision.argentica.databinding.FragmentLoginBinding
import fr.optivision.argentica.ui.fragments.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    //onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.w("Google", "Google sign in failed", e)
            }
        }
    }

    //firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("Google", "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(requireContext(), "Google Sign In Success", Toast.LENGTH_SHORT).show()
                    //navigate to home
                    findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
                    loginViewModel.setTitle("Mon compte")
                } else {
                    Log.w("Google", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupUI(){
        binding.btnSeePassword.setOnClickListener {
            binding.editAccountPassword.transformationMethod = null
            binding.btnHidePassword.visibility = View.VISIBLE
            binding.btnSeePassword.visibility = View.GONE
            //reset cursor position to end
            binding.editAccountPassword.setSelection(binding.editAccountPassword.text.length)
        }

        binding.btnHidePassword.setOnClickListener {
            binding.editAccountPassword.transformationMethod = PasswordTransformationMethod()
            binding.btnHidePassword.visibility = View.GONE
            binding.btnSeePassword.visibility = View.VISIBLE
            //reset cursor position to end
            binding.editAccountPassword.setSelection(binding.editAccountPassword.text.length)
        }

        binding.btnLoginConnect.setOnClickListener{
            connectUser(email = binding.editAccountEmail.text.toString(), password = binding.editAccountPassword.text.toString())
        }

        binding.btnGoogleConnect.setOnClickListener{
            Log.d("Google", "Google")
            GoogleSignIn.getClient(
                requireContext(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build())
                .signInIntent.also {
                    startActivityForResult(it, 100)
                }
        }

        binding.btnRegisterText.setOnClickListener{
            Log.d("Register", "Register")
            binding.btnLoginConnect.visibility = View.GONE
            binding.btnGoogleConnect.visibility = View.GONE
            binding.linearLayout.visibility = View.GONE
            binding.textOr.visibility = View.GONE
            binding.btnRegisterEmail.visibility = View.VISIBLE
            binding.btnGoogleRegister.visibility = View.VISIBLE

        }

        binding.btnRegisterEmail.setOnClickListener{
            registerUser(email = binding.editAccountEmail.text.toString(), password = binding.editAccountPassword.text.toString())
        }
        binding.btnGoogleRegister.setOnClickListener{
            Log.d("Google", "Google")
            //
            GoogleSignIn.getClient(
                requireContext(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build())
                .signInIntent.also {
                    startActivityForResult(it, 100)
                }

        }
    }

    private fun registerUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "User created", Toast.LENGTH_SHORT).show()
                        //navigate to home
                        findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
                        loginViewModel.setTitle("Mon compte")

                    } else {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun connectUser(email: String?, password: String?) {
        //connect user ith firebase
        if (email != null && password != null) {
            if(email.isNotBlank() && password.isNotBlank()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginFragment", "User connected")
                            //navigate to home
                            findNavController().navigate(R.id.action_loginFragment_to_accountFragment)
                            loginViewModel.setTitle("Mon compte")

                        } else {
                            Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            } else {
                Toast.makeText(
                    context, "Email or password is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
