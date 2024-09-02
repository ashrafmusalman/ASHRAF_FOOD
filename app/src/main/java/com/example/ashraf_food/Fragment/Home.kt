package com.example.ashraf_food.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ashraf_food.Adapter.BottomAdapter
import com.example.ashraf_food.Adapter.popularAD
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular
import com.example.ashraf_food.ViewModel.homeViewModel
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentHomeBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Home : Fragment() {
    private lateinit var homeMVVM: homeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popularAdapter: popularAD
    private lateinit var bottomAdapter: BottomAdapter
    private lateinit var BottomAD: BottomAdapter
    private var data: Meal? = null
    private var popularDATA: popular? = null
    private var bottomData: Bottom? = null
    private lateinit var popularList: ArrayList<popular>
    private lateinit var bottom_list: ArrayList<Bottom>
    private lateinit var database: MealDatabse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = MealDatabse.getInstance(requireContext())
        bottom_list = ArrayList()//initialize the bottomList to take data of arrayList
        popularList = ArrayList() // Initialize popularList here



        // Initializing the homeMVVM
        homeMVVM = ViewModelProvider(this)[homeViewModel::class.java]

        // Calling the functions of the class homeViewModel
        homeMVVM.getMutableLiveMeal()
        homeMVVM.getMutableRandomLiveMeal() // Ensure this function is called to fetch popular meals
        homeMVVM.getBottomMeal()


        // Observing LiveData
        listenObserveMutableLiveMealData()
        listenPopularLivemealDATA() // Ensure this function is called to observe the popular meals LiveData
        ListenObserveBottomData()

        // Setting up the RecyclerView
        setupRecyclerView()
        setUpBottomRV()
        onRandomMealClick()

//        when popular image is clicked
        onpopularImageClicked()
        onBottomImageClicked()

        getBottomdataFromRoom()



        binding.homeSearch.setOnClickListener{
            findNavController().navigate(R.id.action_home_to_searchFragment)
        }
    }

    private fun setUpBottomRV() {
        bottomAdapter = BottomAdapter(requireContext(), bottom_list)
        binding.rvBottom.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvBottom.adapter = bottomAdapter


    }


    private fun onpopularImageClicked() {
        popularAdapter.onPopularItemClick = {
            val popularDATA = popularDATA
            val action = HomeDirections.actionHomeToRandom(
                Random = it,
                randomMeal = null,
                BottomArg = null,
                categoryRandom = null
            )
            findNavController().navigate(action)


        }
    }


    private fun onBottomImageClicked() {//the image of the category recycler view
        bottomAdapter.onBottomItemClicked = { value ->
            val categoryName = Bundle().apply {
                putString(
                    "categoryName",
                    value.strCategory
                )// this is how we pass the data to the next fragmet using bundle
            }

            findNavController().navigate(R.id.action_home_to_final_CATEGORY, categoryName)

        }


    }


    private fun setupRecyclerView() {
        popularAdapter = popularAD(requireContext(), popularList)
        binding.rvHorizontalOverPopularItem.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHorizontalOverPopularItem.adapter = popularAdapter
    }

    private fun onRandomMealClick() {
        binding.randomImgCard.setOnClickListener {
            val meal = data
            if (meal != null) {
                Toast.makeText(requireContext(), "${meal.strArea} Food", Toast.LENGTH_SHORT).show()
            }
            if (meal != null) {
                Log.d("HomeFragment", "Random meal clicked: $meal")
                val action = HomeDirections.actionHomeToRandom(
                    randomMeal = meal,
                    Random = null,
                    BottomArg = null,
                    categoryRandom = null

                )
                findNavController().navigate(action)
            } else {
                Log.e("HomeFragment", "Data is not initialized")
            }
        }
    }

    private fun listenObserveMutableLiveMealData() {
        homeMVVM.observeMutableLiveMealData().observe(viewLifecycleOwner, Observer { value ->
            data = value
            Picasso.get().load(value.strMealThumb).into(binding.randomImgCard)
        })
    }

    private fun listenPopularLivemealDATA() {
        homeMVVM.observeRandomMutableLiveData().observe(viewLifecycleOwner, Observer { value ->
            popularList.clear()
            popularList.addAll(value)
            popularAdapter.notifyDataSetChanged()
        })
    }

    //observer for the category meal of home fragment
    private fun ListenObserveBottomData() {
        homeMVVM.observeBottomMeal().observe(viewLifecycleOwner) { value ->

            CoroutineScope(Dispatchers.IO).launch {
                value.forEach {
                    database.mealDao().insertCategory(it)
                }

            }

        }



    }
    //getting the data from the room database
    fun getBottomdataFromRoom() {
        val bottomFormRoom = database.mealDao().getAllBottom()
        bottomFormRoom.observe(viewLifecycleOwner, Observer {
            bottom_list.clear()
            bottom_list.addAll(it)
            bottomAdapter.notifyDataSetChanged()

        })
    }




}


