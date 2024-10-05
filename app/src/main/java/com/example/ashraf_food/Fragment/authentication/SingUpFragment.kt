package com.example.ashraf_food.Fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ashraf_food.Activity.MainActivity
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentSingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SingUpFragment : Fragment() {

    private lateinit var binding: FragmentSingUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        progress = binding.progressBarr

        binding.btnSignUp.setOnClickListener {
            setUpSignUp()
            progress.visibility = View.VISIBLE
        }

        binding.tvAskingLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun setUpSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val userName = binding.etUserName.text.toString().trim()  // Get username from input

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || userName.isEmpty()) {
            progress.visibility = View.GONE
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Enter a valid Email", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isVlaidPassword(password)) {
            Toast.makeText(
                requireContext(),
                "Password must have at least one special character, one Uppercase, and one digit",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password != confirmPassword) {
            progress.visibility = View.GONE
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        // Set displayName using updateProfile
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(userName)
                            .build()

                        firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "SignUp Successful", Toast.LENGTH_SHORT).show()
                                    progress.visibility = View.GONE
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    progress.visibility = View.GONE
                                    Log.e("SignUp", "Failed to update profile: ${profileTask.exception?.message}")
                                    Toast.makeText(requireContext(), "Failed to save username", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    progress.visibility = View.GONE
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Toast.makeText(requireContext(), "SignUp Failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isVlaidPassword(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        return hasUpper && hasDigit && hasLetter && hasSpecial
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf('.')
    }
}
