package com.example.ashraf_food.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ashraf_food.Dataclass.CartMeal
import com.example.ashraf_food.Dataclass.popular
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesAdapter(
    private val context: Context,
    private val cartMeals: MutableList<CartMeal>
) : RecyclerView.Adapter<CategoriesAdapter.CartViewHolder>() {

    private val db: MealDatabse by lazy {
        MealDatabse.getInstance(context)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image1: ImageView = itemView.findViewById(R.id.cartImage)
        val textOfCart: TextView = itemView.findViewById(R.id.cartFoodName)
        val plus: ImageButton = itemView.findViewById(R.id.cartPlus)
        val cartIncreaseDecreaseText: TextView = itemView.findViewById(R.id.textView)
        val minus: ImageButton = itemView.findViewById(R.id.cartMinus)
        val delete: ImageView = itemView.findViewById(R.id.deleteCartItem)
        val cartFoodPrice: TextView = itemView.findViewById(R.id.cartFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.cart_item_design, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentCartMeal = cartMeals[position]

        // Load image and name
        currentCartMeal.popular?.let {
            Picasso.get().load(it.strMealThumb).into(holder.image1)
            holder.textOfCart.text = it.strMeal
        } ?: currentCartMeal.meal?.let {
            Picasso.get().load(it.strMealThumb).into(holder.image1)
            holder.textOfCart.text = it.strMeal
        }

        // Initialize quantity and price
        holder.cartIncreaseDecreaseText.text = currentCartMeal.quantity.toString()
        holder.cartFoodPrice.text = currentCartMeal.totalPrice.toString()//  setting initial  price

        // Set click listener for Plus button
        holder.plus.setOnClickListener {
            // Update quantity
            currentCartMeal.quantity += 1
            holder.cartIncreaseDecreaseText.text = currentCartMeal.quantity.toString()

            currentCartMeal.totalPrice = currentCartMeal.totalPrice + 25
            holder.cartFoodPrice.text =
                currentCartMeal.totalPrice.toString()// here we have set the price after increasing the quantity

            // Update the database
            updateDatabase(currentCartMeal)
        }

        // Set click listener for Minus button
        holder.minus.setOnClickListener {
            if (currentCartMeal.quantity > 1) {
                // Update quantity
                currentCartMeal.quantity -= 1
                holder.cartIncreaseDecreaseText.text = currentCartMeal.quantity.toString()

                // Update total price
                currentCartMeal.totalPrice = currentCartMeal.totalPrice - 25
                holder.cartFoodPrice.text =
                    currentCartMeal.totalPrice.toString()// after decreasing the price we need to update the price

                // Update the database
                updateDatabase(currentCartMeal)
            }
        }

        // Set click listener for Delete button
        holder.delete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.cartMealDao().delete(currentCartMeal)
                CoroutineScope(Dispatchers.Main).launch {
                    val removedPosition = cartMeals.indexOf(currentCartMeal)
                    if (removedPosition != -1) {
                        cartMeals.removeAt(removedPosition)
                        notifyItemRemoved(removedPosition)
                        notifyItemRangeChanged(removedPosition, cartMeals.size)
                    }
                }
            }
        }






    }

    // Function to update the database with the new CartMeal
    private fun updateDatabase(cartMeal: CartMeal) {
        CoroutineScope(Dispatchers.IO).launch {
            db.cartMealDao().updateCart(cartMeal)
        }
    }

    override fun getItemCount(): Int {
        return cartMeals.size
    }



}