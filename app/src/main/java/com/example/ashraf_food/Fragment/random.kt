package com.example.ashraf_food.Fragment

import android.content.Context
import android.content.Intent

import android.os.SystemClock
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentRandomBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class random : Fragment() {

    private val args: randomArgs by navArgs()
    private lateinit var binding: FragmentRandomBinding
    var lastClickTime: Long = 0
    val DOUBLE_CLICK_TIME_DELTA: Long = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRandomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //make the bottom navigation to hide when the fragement is created
        val bottomNavigationView =       activity?.findViewById<BottomNavigationView>(R.id.bottm_navigation)
        bottomNavigationView?.visibility = View.GONE

        val ak = args.randomMeal//random meal
        val random = args.Random//popular meal
        val bottom = args.BottomArg
        val final_cat_send = args.categoryRandom


        //////MAKE THIS FUNCTION TO ALLOW THE DOUBLE CLICK FOR THE HERART IMAGE AND USE THIS FUNCIOTN IN YOUR binding.heart.setOnDoubleCLickListner()
        //mthod to detect double click listner
        fun View.setOnDoubleClickListener(doubleClickListener: (View) -> Unit) {
            var lastClickTime: Long =
                0//this two varibale check the interval between two consecutive click
            val DOUBLE_CLICK_TIME_DELTA: Long = 200 // milliseconds

            this.setOnClickListener {
                val clickTime = SystemClock.elapsedRealtime()
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    doubleClickListener(this)
                }
                lastClickTime = clickTime
            }
        }


// custome funtion TO CHANGE THE COLOR OF HEART
        binding.collapsingToolbar.setOnDoubleClickListener {
            val customRedColor = ContextCompat.getColor(requireContext(), R.color.primary)
            binding.heart.setColorFilter(customRedColor)

            ak?.let {
                lifecycleScope.launch {
                    MealDatabse.getInstance(requireContext()).mealDao().insert(it)
                    Toast.makeText(requireContext(), "Meal saved successfull", Toast.LENGTH_SHORT)
                        .show()

                }
            }


            random?.let {
                lifecycleScope.launch {
                    MealDatabse.getInstance(requireContext()).mealDao().insertPopular(it)
                    Toast.makeText(requireContext(), "Meal saved successfull", Toast.LENGTH_SHORT)
                        .show()

                }
            }



            final_cat_send?.let {
                lifecycleScope.launch {
                    MealDatabse.getInstance(requireContext()).mealDao().insertBottom(it)
                    Toast.makeText(requireContext(), "Meal saved successfull", Toast.LENGTH_SHORT)
                        .show()

                }
            }


        }


        //putting ak.let to reduce diffferent if else sttement and binding the data to the view

        ak?.let {//TO REDUCE IF ELSE STATEMENT
            binding.realText.text = it.strMeal
            binding.category.text = it.strCategory
            binding.location.text = it.strArea
            binding.descriptonOfMeal.text = it.strInstructions
            loadImage(it.strMealThumb, binding.toolbarImgRandom)

        }

        final_cat_send?.let {
            binding.realText.text = it.strMeal
            binding.location.text = it.idMeal
            loadImage(it.strMealThumb, binding.toolbarImgRandom)


        }

        random?.let {
            binding.realText.text = it.strMeal
            binding.location.text = it.idMeal
            loadImage(it.strMealThumb, binding.toolbarImgRandom)
        }

        if (ak?.strCategory == null) {
            binding.category.text = " 😔 No Description"
        }

        binding.youtube.setOnClickListener {
            val youtubeUrl = ak?.strYoutube
            if (youtubeUrl != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Link not available", Toast.LENGTH_SHORT).show()
            }
        }

        bottom?.let {
            binding.realText.text = it.strCategory
            binding.location.text = it.idCategory
            binding.category.text = it.strCategory
            binding.descriptonOfMeal.text = it.strCategoryDescription
            loadImage(it.strCategoryThumb, binding.toolbarImgRandom)
        }


    }

    private fun loadImage(url: String?, imageView: ImageView) {
        if (url != null) {
            Picasso.get()
                .cancelRequest(imageView)//this is the best method to load the images as we cancel the previous request of image and then pass he next image

            Picasso.get().load(url).into(imageView)
            Picasso.get().load(url).into(imageView)
        }
    }


    //database related operation


    override fun onDestroyView() {
        super.onDestroyView()

        // Show the BottomNavigationView when the fragment is destroyed
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottm_navigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }


}
