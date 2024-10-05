package com.example.ashraf_food.Fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ashraf_food.Activity.Login
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentProfileSectioonBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class Profile_Sectioon : Fragment() {

    private lateinit var binding: FragmentProfileSectioonBinding
    private var fileUri: Uri? = null
    private val storageReference = FirebaseStorage.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedprefernceData: SharedPreferences
    private val addressKey = "address_key"

    // Code to select an image from the gallery for profile image
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            fileUri = uri
            uploadProfileImageToFireStorage() // Upload for profile image
        }

    // Code to select an image from the gallery for banner image
    private val bannerImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            fileUri = uri
            uploadImageToBanner() // Upload for banner image
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        sharedprefernceData=requireActivity().getSharedPreferences("userpreferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileSectioonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Open gallery when the profile image is clicked
        binding.profileImage.setOnClickListener {
            openGalleryForProfile()
        }

        // Open gallery when the edit pen is clicked to change the banner
        binding.profileEditPen.setOnClickListener {
            openGalleryForBanner()
        }


        binding.profileBack.setOnClickListener {
            findNavController().navigate(R.id.action_profile_Section_to_home)
            requireActivity().finish()


        }
        // Load previously uploaded images (if any)
        setPreviouslyUploadedProfileImage()
        setPreviouslyUploadedBannerImage()

        LogoutFunctionality()
        Making_EditFunctionlity_OnClickOf_EditButton()
        loadAddress()

        binding.textInputAddress.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveAddress(binding.textInputAddress.text.toString())
            }
        }

    }

    // Function to open the gallery for profile image
    private fun openGalleryForProfile() {
        imageLauncher.launch("image/*")
    }

    // Function to open the gallery for banner image
    private fun openGalleryForBanner() {
        bannerImageLauncher.launch("image/*")
    }

    // Function to upload the selected profile image to Firebase Storage
    private fun uploadProfileImageToFireStorage() {
        //showCustomProgressDialog()
        binding.progressBar.visibility = View.VISIBLE

        if (fileUri != null) {
            // Storage path for profile image  , get the image reference
            //  profileImages  ==> it is nothing but a folder created in firestorage
            val imageReference =
                storageReference.child("profileImages/${auth.currentUser?.uid}.jpg")

            imageReference.putFile(fileUri!!)// !! ==> file cant be null
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->   // this uri is the url of the image of firestorage
                        val downloadURL = uri.toString()// download url of the image
                        Log.d("ProfileFragment", "Profile Image URL: $downloadURL")

                        // Load the profile image into the ImageView
                        auth.currentUser?.let { user ->
                            val profileUpdate = UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(downloadURL))
                                .build()


                            user.updateProfile(profileUpdate).addOnCompleteListener { task ->

                                if (task.isSuccessful) {
                                    Glide.with(requireContext())
                                        .load(downloadURL)
                                        .placeholder(R.drawable.ak)
                                        .into(binding.profileImage)
                                    binding.progressBar.visibility = View.GONE

                                    Toast.makeText(
                                        requireContext(),
                                        "Updated Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "failed to update",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }
                        }



                        Toast.makeText(
                            requireContext(),
                            "Profile Image successfully uploaded!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Profile Image Upload failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                requireContext(),
                "No file selected for profile image!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Function to upload the selected banner image to Firebase Storage
    private fun uploadImageToBanner() {

        binding.progressBar.visibility = View.VISIBLE
        if (fileUri != null) {
            val bannerReference =
                storageReference.child("bannerImages/${auth.currentUser?.uid}.jpg")

            bannerReference.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val downloadURL = uri.toString()
                        Log.d("ProfileFragment", "Banner Image URL: $downloadURL")

                        // Save the banner URL to Firestore
                        //here we have to use the firestore as the
                        val userBannerData = hashMapOf("bannerUrl" to downloadURL)
                        FirebaseFirestore.getInstance().collection("users")
                            .document(auth.currentUser?.uid ?: "")
                            .set(userBannerData)
                            .addOnSuccessListener {
                                // Load the banner image into the ImageView
                                Glide.with(binding.profileBanner.context)
                                    .load(downloadURL)
                                    .into(binding.profileBanner)


                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Banner successfully uploaded!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to save banner URL: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.progressBar.visibility = View.GONE

                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Banner Upload failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE

                }
        } else {
            Toast.makeText(requireContext(), "No file selected for banner!", Toast.LENGTH_SHORT)
                .show()
            binding.progressBar.visibility = View.GONE

        }
    }

    // Load previously uploaded profile image
    private fun setPreviouslyUploadedProfileImage() {
        val photoUri = auth.currentUser?.photoUrl
        val user = auth.currentUser
        val profileEmail = user?.email
        binding.textInputEmail.setText(profileEmail) // Set the email in the EditText


        // Set the user name if available
        binding.textInputName.setText(user?.displayName)

        // Check if there is a profile image URI available
        if (photoUri != null) {
            // Load the profile image into the ImageView using Glide
            Glide.with(binding.profileImage.context)
                .load(photoUri)
                .placeholder(R.drawable.ak) // Placeholder image
                .into(binding.profileImage)
        }
        // No toast message here for new users without an image
    }

    //  //➡️➡️  Load previously uploaded banner image
    private fun setPreviouslyUploadedBannerImage() {
        // Fetch the saved banner URL from Firestore
        FirebaseFirestore.getInstance().collection("users")
            .document(auth.currentUser?.uid ?: "")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val bannerUrl = document.getString("bannerUrl")
                    if (!bannerUrl.isNullOrEmpty()) {
                        // Load the banner image into the ImageView using Glide
                        Glide.with(binding.profileBanner.context)
                            .load(bannerUrl)
                            .into(binding.profileBanner)
                    }
                    // No toast message if the banner image is not present for a new user
                }
            }
            .addOnFailureListener { e ->
                // Display an error message only for genuine failures, not for missing data
            }
    }

    //➡️➡️  LOG OUT
    private fun LogoutFunctionality() {
        binding.LogOutBtn.setOnClickListener {
            // Inflate the custom layout for the dialog
            val logOutDesign = layoutInflater.inflate(R.layout.logout_dialgo_box_design, null)

            // Create the dialog and set the content view to the inflated layout
            val dialog = Dialog(requireContext())

            // Set the dialog's background to transparent to show the CardView’s rounded corners
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.setContentView(logOutDesign)

            // Set dialog width to match parent and height to wrap content
            val layoutParams = dialog.window?.attributes
            layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams?.height = 600
            dialog.window?.attributes = layoutParams

            // Set custom margins to the dialog view
            val marginParams = logOutDesign.layoutParams as ViewGroup.MarginLayoutParams
            marginParams.setMargins(20, 0, 20, 0) // Left, Top, Right, Bottom margins (in pixels)
            logOutDesign.layoutParams = marginParams

            // Show the dialog
            dialog.show()

            // Set up the buttons inside the inflated view
            logOutDesign.findViewById<Button>(R.id.btnYes).setOnClickListener {
                auth.signOut()
                dialog.dismiss()
                navigateToLoginActivity()
            }

            logOutDesign.findViewById<Button>(R.id.btnNo).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun navigateToLoginActivity() {
        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }


    private fun Making_EditFunctionlity_OnClickOf_EditButton() {
        var isEditMode = true  // Initial state is Edit mode  assume it like a flag to track the click functionality

        binding.profileEditBtn.setOnClickListener {

            if (isEditMode) {// it means true so thee code inside the if is executed if the condition is true
                // Switch to Edit Mode: Enable editing for the EditText fields
                binding.textInputName.isFocusableInTouchMode = true
                binding.textInputEmail.isFocusableInTouchMode = true// enable the edit mode
                binding.textInputAddress.isFocusableInTouchMode = true

                // Set full visibility for the EditText fields
                binding.textInputName.alpha = 1.0f
                binding.textInputEmail.alpha = 1.0f// setting alpha to 1 means that we hae allowed the text to be bright which was initailly faded odue to low alpha
                binding.textInputAddress.alpha = 1.0f

                // Change the edit text  to "Save"
                binding.profileEditBtn.text = "Save"

                // Update the mode to "Save"
                isEditMode = false// initially  it is false that is not clicked so it is in edit mode

            } else {
                // Switch to Save Mode: Disable editing for the EditText fields
                binding.textInputName.isFocusable = false
                binding.textInputEmail.isFocusable = false
                binding.textInputAddress.isFocusable = false

                binding.textInputName.isFocusableInTouchMode = false
                binding.textInputEmail.isFocusableInTouchMode = false
                binding.textInputAddress.isFocusableInTouchMode = false

                // Show Snackbar when save button is clicked
                Snackbar.make(it, "Profile saved successfully", Snackbar.LENGTH_SHORT).show()

                // Change the button text back to "Edit"
                binding.profileEditBtn.text = getString(R.string.edit_profile)
                binding.profileEditBtn.setBackgroundResource(R.drawable.log_out_btn)  // Optional background reset

                // Update the mode back to "Edit"
                isEditMode = true
            }
        }
    }



    private fun saveAddress(address: String) {
        val editor = sharedprefernceData.edit()
        editor.putString(addressKey, address)
        editor.apply()
    }

    // Function to load address from SharedPreferences
    private fun loadAddress() {
        val savedAddress = sharedprefernceData.getString(addressKey, "")
        binding.textInputAddress.setText(savedAddress)
    }

}
