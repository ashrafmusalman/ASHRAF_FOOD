package com.example.ashraf_food.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ashraf_food.Dataclass.CartMeal
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular
import com.example.ashraf_food.db.MealDatabse
import com.example.foodapp.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedMeal(val context: Context, var combinedData: ArrayList<Any>) :
    RecyclerView.Adapter<SavedMeal.SavedInnerClass>() {
    private val db: MealDatabse by lazy {//way to initialize the database
        MealDatabse.getInstance(context)
    }

    class SavedInnerClass(view: View) : RecyclerView.ViewHolder(view) {
        val savedImage: ImageView = view.findViewById(R.id.saved_image)
        val savedImageText: TextView = view.findViewById(R.id.saved_text)
        val button: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedInnerClass {
        val itemView = LayoutInflater.from(context).inflate(R.layout.saved_eal_esign, parent, false)
        return SavedInnerClass(itemView)
    }

    override fun getItemCount(): Int {
        return combinedData.size
    }

    override fun onBindViewHolder(holder: SavedInnerClass, position: Int) {
        val item = combinedData[position]
        if (item is Meal) {
            Picasso.get().load(item.strMealThumb).into(holder.savedImage)
            holder.savedImageText.text = item.strMeal
        } else if (item is popular) {
            Picasso.get().load(item.strMealThumb).into(holder.savedImage)
            holder.savedImageText.text = item.strMeal
        }

        holder.itemView.setOnLongClickListener {
            showOptionsDialog(item)
            true

        }

        holder.button.setOnClickListener {
            if (item is Meal) {
                onclick?.invoke(null, item)
            } else if (item is popular) {
                onclick?.invoke(item, null)
            }
        }
    }


    private fun showOptionsDialog(item: Any) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Are You Sure To Delete Food?")

        dialog.setPositiveButton("Yes") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                when (item) {
                    is Meal -> db.mealDao().delete(item)
                    is popular -> db.mealDao()
                        .deletepopular(item) // Add deletePopular method in your DAO
                }
            }
        }

        dialog.setNegativeButton("NO") { dialog, _ ->
            dialog.dismiss()
        }


        dialog.show()
    }

    var onclick: ((popular?, Meal?) -> Unit)? = null

//    private fun deleteMeal(meal: Meal, position: Int) {
//      CoroutineScope(Dispatchers.IO).launch {
//            db.mealDao().delete(meal)
//
//
//        }
//    }
}
