package com.example.ashraf_food.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ashraf_food.Adapter.BottomAdapter
import com.example.ashraf_food.Adapter.category_img_click_AD
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.ViewModel.category_clk
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.databinding.FragmentCategoriesBinding
import com.example.foodapp.databinding.FragmentFinalCATEGORYBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Random

class final_CATEGORY : Fragment() {
    private lateinit var binding: FragmentFinalCATEGORYBinding
    private lateinit var category_click_MVVM: category_clk
    private lateinit var cat_img_clk_AD: category_img_click_AD
    private lateinit var bottomAdapter: BottomAdapter
    private lateinit var cat_img_clk_lst: ArrayList<Category>
    private lateinit var database:MealDatabse
private  var finaldata:Category?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using the binding class
        binding = FragmentFinalCATEGORYBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database= MealDatabse.getInstance(requireContext())

        cat_img_clk_lst = ArrayList()
        category_click_MVVM = ViewModelProvider(this)[category_clk::class.java]



        setupCategoryClickeRV()

        val categoryName = arguments?.getString("categoryName")///THIS IS HOW WE GET SOMETING IN FRAGMENT
        Log.d("categoryName", categoryName.toString())
        categoryName?.let {
            // Handle the category name
            category_click_MVVM.getCategoryMEAL(it)
        }
        if (categoryName != null) {
            ListenobserveCategory_img_clk(categoryName)
        }

        onFinalCatImageClick()

        if(categoryName!= null){
           val cat= database.mealDao().getCategory(categoryName)
            cat.observe(viewLifecycleOwner, Observer {
                Log.d("catgorydaaaaa",it.toString())
                cat_img_clk_lst.clear()
                cat_img_clk_lst.addAll(it)
                cat_img_clk_AD.notifyDataSetChanged()
            })
        }

    }

    private fun onFinalCatImageClick() {
        cat_img_clk_AD.on_ctgry_moved_item = {
            finaldata = it // Update finaldata with the clicked item

            finaldata?.let {
                val action = final_CATEGORYDirections.actionFinalCATEGORYToRandom(
                    Random = null,
                    randomMeal = null,
                    BottomArg = null,
                    categoryRandom = it
                )
                findNavController().navigate(action)
            } ?: run {
                Log.d("NavigationError", "finaldata is null")
                // Handle the case when finaldata is null
            }
        }
    }


    private fun setupCategoryClickeRV() {
        cat_img_clk_AD = category_img_click_AD(requireContext(), cat_img_clk_lst)
        // Ensure you are referencing the correct RecyclerView ID from the binding class
        binding.rvFinal.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvFinal.adapter = cat_img_clk_AD
    }

    private fun ListenobserveCategory_img_clk(categoryName: String) {
        category_click_MVVM.ObserveCategoryMEAL()
            .observe(viewLifecycleOwner, object : Observer<List<Category>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChanged(value: List<Category>) {

                    CoroutineScope(Dispatchers.IO).launch {
                        value.forEach{
                            val categMeal = it.copy(strCategory = categoryName)
                            database.mealDao().insertBottom(categMeal)
                        }

                    }

                    cat_img_clk_lst.clear()
                    cat_img_clk_lst.addAll(value)
                    cat_img_clk_AD.notifyDataSetChanged()
                }
            })
    }
}
