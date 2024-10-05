package com.example.ashraf_food.Fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ashraf_food.Activity.MainActivity
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.tvAskingSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_singUpFragment)

        }

        binding.loginBtn.setOnClickListener {
            val email = binding.logInEtEmail.text.toString().trim()
            val password = binding.loginEtPassword.text.toString().trim()
            binding.progressBar.visibility = View.VISIBLE



            if (!isValidEmail(email)) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Enter a valid Email", Toast.LENGTH_SHORT).show()

            } else if (email.isEmpty() || password.isEmpty()) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Enter a valid Email and Password",
                    Toast.LENGTH_SHORT
                ).show()


            } else {

                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(
                                requireContext(),
                                "signIn successful",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please check Your Email and Password",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE
                        }

                    }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf(
            '.'
        )
    }

    fun goToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            goToMainActivity()
        }
    }


}