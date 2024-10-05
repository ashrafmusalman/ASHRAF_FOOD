package com.example.ashraf_food.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.popular
import com.example.foodapp.R
import com.squareup.picasso.Picasso

class category_img_click_AD(val context: Context, private val category_click: ArrayList<Category>) :
    RecyclerView.Adapter<category_img_click_AD.BottomInnerClas>() {

    var on_ctgry_moved_item: ((Category) -> Unit)? = null

    fun updateData(newData: List<Category>) {
        category_click.clear()
        category_click.addAll(newData)
        notifyDataSetChanged()
    }

    class BottomInnerClas(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        design
        val catTxt: TextView = itemView.findViewById(R.id.cat_clk_tv)
        val image1: ImageView = itemView.findViewById(R.id.cat_clk_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomInnerClas {
        val itemview = LayoutInflater.from(context).inflate(R.layout.cat_img_clk_design, parent, false)
        return BottomInnerClas(itemview)



    }

    override fun getItemCount(): Int {
        return category_click.size
    }


    override fun onBindViewHolder(holder: BottomInnerClas, position: Int) {
        val currentBottomItem = category_click[position]
        Picasso.get().load(currentBottomItem.strMealThumb).into(holder.image1)
        holder.catTxt.text=currentBottomItem.strMeal
        holder.itemView.setOnClickListener{
            on_ctgry_moved_item?.invoke(currentBottomItem)
        }
    }

}