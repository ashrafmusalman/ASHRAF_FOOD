package com.example.ashraf_food.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular
import com.example.foodapp.R
import com.squareup.picasso.Picasso

class popularAD(val context: Context, private val bottomCategory: ArrayList<popular>) :
    RecyclerView.Adapter<popularAD.BottomInnerClas>() {


    var onPopularItemClick: ((popular) -> Unit)? = null

    class BottomInnerClas(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image1: ImageView = itemView.findViewById(R.id.popularImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomInnerClas {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.rv_meal_ategoriies_item_design, parent, false)
        return BottomInnerClas(itemView)
    }

    override fun getItemCount(): Int {
        return bottomCategory.size
    }

    override fun onBindViewHolder(holder: BottomInnerClas, position: Int) {
        val currentBottomItem = bottomCategory[position]
        Picasso.get().load(currentBottomItem.strMealThumb).into(holder.image1)

        Log.d("Image URL", "Loading URL: ${currentBottomItem.strMealThumb}")


        holder.itemView.setOnClickListener {
            onPopularItemClick?.invoke(currentBottomItem)


        }
    }


}
