package com.example.ashraf_food.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ashraf_food.Dataclass.Bottom
import com.example.foodapp.R
import com.squareup.picasso.Picasso
import org.w3c.dom.Text


class BottomAdapter(val context: Context, private val bottomCategory: ArrayList<Bottom>) :
    RecyclerView.Adapter<BottomAdapter.BottomInnerClas>() {


    private var filteredBottomCategory: List<Bottom> = bottomCategory
    // Filter method to filter the quizzes based on the title

    fun filter(query: String) {
        filteredBottomCategory = if (query.isEmpty()) {
            bottomCategory // Original data
        } else {
            bottomCategory.filter { it.strCategory.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    var onBottomItemClicked: ((Bottom) -> Unit)? = null


    class BottomInnerClas(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        design
        val btmTxt: TextView = itemView.findViewById(R.id.bottom_rv_text)
        val image1: ImageView = itemView.findViewById(R.id.bottom_rv_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomInnerClas {
        val itemview =
            LayoutInflater.from(context).inflate(R.layout.bottom_rv_item_desing, parent, false)
        return BottomInnerClas(itemview)

    }

    override fun getItemCount(): Int {
        return filteredBottomCategory.size
    }


    override fun onBindViewHolder(holder: BottomInnerClas, position: Int) {
        val currentBottomItem = filteredBottomCategory[position]
        Picasso.get().load(currentBottomItem.strCategoryThumb).into(holder.image1)
        holder.btmTxt.text = currentBottomItem.strCategory
        holder.itemView.setOnClickListener {
            onBottomItemClicked?.invoke(currentBottomItem)
        }
    }

}
