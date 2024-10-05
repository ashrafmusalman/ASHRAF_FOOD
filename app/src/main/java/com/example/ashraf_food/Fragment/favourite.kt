package com.example.ashraf_food.Fragment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ashraf_food.Adapter.SavedMeal
import com.example.ashraf_food.Dataclass.CartMeal
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentFavouriteBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class favourite : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var database: MealDatabse
    private lateinit var savedAD: SavedMeal
    private lateinit var savedData: ArrayList<Meal>
    private lateinit var combinedData: ArrayList<Any>
    private lateinit var savedPopualrData: ArrayList<popular>
    private lateinit var finalcat: ArrayList<Category>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedPopualrData = ArrayList()
        savedData = ArrayList()
        combinedData = ArrayList()
        finalcat = ArrayList()
        database = MealDatabse.getInstance(requireContext())

        setupRv()
        observeData()
        observePopularData()
        observCategoryrData()

        savedAD.onclick = { popular, meal ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {

                if (popular != null) {
                    database.cartMealDao().insertCartMeal(
                            CartMeal(
                                popular = popular,
                                meal = null,
                                plusData = 0,
                                minusData = 0,
                                totalPrice = 25,
                                quantity = 1


                            )
                    )
                }

                if (meal != null) {
                    database.cartMealDao().insertCartMeal(
                        CartMeal(
                            popular = null,
                            meal = meal,
                            plusData = 0,
                            minusData = 0,
                            totalPrice = 0,
                            quantity = 1

                        )
                    )
                }

            }
        }

        binding.savedRc.setOnLongClickListener {
            Toast.makeText(requireContext(), "long pressed", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        // Attach ItemTouchHelper to RecyclerView for swiping left and right
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.savedRc)
    }

    // Setup RecyclerView
    private fun setupRv() {
        savedAD = SavedMeal(requireContext(), combinedData)
        binding.savedRc.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.savedRc.adapter = savedAD
    }

    private fun observeData() {
        database.mealDao().getAllMeal().observe(viewLifecycleOwner, Observer {
            savedData.clear()
            savedData.addAll(it.reversed())
            updateCombinedData()
        })
    }

    private fun observePopularData() {
        database.mealDao().getAllPopular().observe(viewLifecycleOwner, Observer {
            savedPopualrData.clear()
            savedPopualrData.addAll(it)
            updateCombinedData()
        })
    }

    private fun observCategoryrData() {
        database.mealDao().getCategory("finalcat").observe(viewLifecycleOwner, Observer {
            finalcat.clear()
            finalcat.addAll(it)
            updateCombinedData()
        })
    }

    private fun updateCombinedData() {
        combinedData.clear()
        combinedData.addAll(savedData)
        combinedData.addAll(savedPopualrData.reversed())
        combinedData.addAll(finalcat)
        savedAD.notifyDataSetChanged()
    }


    // This is for swiping left and right


    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition // Find the position of the item
            val item = combinedData[position] // Find the item based on the position
            val deletedItem = item

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    when (item) {
                        is popular -> {
                            database.mealDao().deletepopular(item)
                            savedPopualrData.remove(item)
                        }

                        is Meal -> {
                            database.mealDao().delete(item)
                            savedData.remove(item)
                        }
                    }

                    // Ensure Snackbar is shown on the main thread
                    withContext(Dispatchers.Main) {
                        combinedData.removeAt(position)
                        savedAD.notifyItemRemoved(position)

                        val snackbar =
                            Snackbar.make(requireView(), "Item deleted", Snackbar.LENGTH_LONG)
                        snackbar.setBackgroundTint(resources.getColor(R.color.primary))

                                .setAction("Undo") {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        try {
                                            when (deletedItem) {
                                                is Meal -> {
                                                    database.mealDao().insert(deletedItem)
                                                    savedData.add(deletedItem)
                                                }

                                                is popular -> {
                                                    database.mealDao().insertPopular(deletedItem)
                                                    savedPopualrData.add(deletedItem)
                                                }
                                            }

                                            // Update UI on the main thread
                                            //we need to give the snackbar on the main thread
                                            withContext(Dispatchers.Main) {
                                                combinedData.add(position, deletedItem)
                                                savedAD.notifyItemInserted(position)
                                            }
                                        } catch (e: Exception) {
                                            Log.e("favourite", "Error restoring item: $e")
                                        }
                                    }
                                }




                        val snackbarView = snackbar.view
                        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                        textView.setTextColor(resources.getColor(R.color.white))
                        snackbar.show()
                    }
                } catch (e: Exception) {
                    Log.e("favourite", "Error deleting item: $e")
                    withContext(Dispatchers.Main) {
                        savedAD.notifyItemChanged(position)
                    }
                }
            }
        }
    }

}

