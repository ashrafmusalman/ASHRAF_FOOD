package com.example.ashraf_food.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ashraf_food.Adapter.SavedMeal
import com.example.ashraf_food.Adapter.category_img_click_AD
import com.example.ashraf_food.ViewModel.category_clk
import com.example.ashraf_food.ViewModel.homeViewModel
import com.example.ashraf_food.ViewModel.savedVM
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentSearchBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job


class SearchFragment : Fragment() {


    private lateinit var binding: FragmentSearchBinding

    private lateinit var catClick:category_clk
    private lateinit var searchMeal:SavedMeal
    private lateinit var searchADAPTER: category_img_click_AD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // homeVM = ViewModelProvider(requireActivity()).get(homeViewModel::class.java)
        catClick = ViewModelProvider(requireActivity()).get(category_clk::class.java)


        setUpSearchRV()
        listenObserveSearchData()

        binding.arrow.setOnClickListener {

        }
    }




    private fun setUpSearchRV() {
        searchADAPTER = category_img_click_AD(requireContext(), ArrayList())
        binding.rvSearch.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvSearch.adapter = searchADAPTER
    }

    private fun listenObserveSearchData() {
        catClick.ObserveCategoryMEAL().observe(viewLifecycleOwner, Observer { searchResults ->
            searchADAPTER.updateData(searchResults)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()

        // Show the BottomNavigationView when the fragment is destroyed
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottm_navigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }

}