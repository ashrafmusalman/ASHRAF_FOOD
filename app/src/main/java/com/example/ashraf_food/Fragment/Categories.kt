package com.example.ashraf_food.Fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashraf_food.Adapter.CategoriesAdapter
import com.example.ashraf_food.Adapter.SavedMeal
import com.example.ashraf_food.Dataclass.CartMeal
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular
import com.example.ashraf_food.db.MealDatabse

import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentCategoriesBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log


class Categories : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var database: MealDatabse
    private lateinit var cartList: ArrayList<CartMeal>
    private lateinit var cartAdapter: CategoriesAdapter
    private lateinit var combinedData: ArrayList<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartList = ArrayList()
        combinedData = ArrayList()
        cartAdapter = CategoriesAdapter(requireContext(), cartList)
        setupCartMealRV()
        database = MealDatabse.getInstance(requireContext())
        fetchCartMeal()

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcyvlerview)
    }

    private fun setupCartMealRV() {
        binding.rcyvlerview.layoutManager = LinearLayoutManager(requireContext())
        val adapter = cartAdapter
        binding.rcyvlerview.adapter = adapter
    }

    private fun fetchCartMeal() {
        viewLifecycleOwner.lifecycleScope.launch {
            val cartmeal = withContext(Dispatchers.IO) {
                database.cartMealDao().getAllCartMeals()
            }
            Log.d("cart Data", cartmeal.toString())
            cartList.clear()
            cartList.addAll(cartmeal)
            cartAdapter.notifyDataSetChanged()
        }
    }


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
            val Cartitem = cartList[position] // Use cartList instead of combinedData


            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    if (Cartitem.popular != null) {

                        database.mealDao().deletepopular(Cartitem.popular)
                    } else if (Cartitem.meal != null) {
                        database.mealDao().delete(Cartitem.meal)
                    }



                    withContext(Dispatchers.Main) {// CHAING THE THREAAD
                        cartList.removeAt(position)
                        cartAdapter.notifyItemRemoved(position)
                        val snackbar = Snackbar.make(binding.root, "Item removed from cart", Snackbar.LENGTH_LONG)
                        snackbar.setBackgroundTint(resources.getColor(R.color.primary)) // Set the background color to red

// Change the text color to white
                        val snackbarView = snackbar.view
                        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                        textView.setTextColor(resources.getColor(R.color.white)) // Set the text color to white

                        snackbar.show()



                    }
                } catch (e: Exception) {
                    Log.e("favourite", "Error deleting item: $e")
                    withContext(Dispatchers.Main) {
                        cartAdapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }


}