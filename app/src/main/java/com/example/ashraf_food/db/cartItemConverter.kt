package com.example.ashraf_food.db

import androidx.room.TypeConverter
import com.example.ashraf_food.Dataclass.CartMeal
import com.google.gson.Gson

//we use the type converter as the api may have some data type like date which the room
// databse doesnot support so it may cause error , so we need to convert the data into the format that the
// room database support like into string or int

class cartItemConverter {
    @TypeConverter
    fun cartMealToString(attributes: CartMeal): String {
        if (attributes == null) {
            return ""
        } else {
            return attributes as String
        }
    }


    @TypeConverter
    fun StringToCartMeal(attributes: String): Any {
        return if (attributes == null)
            return ""
        else
            return attributes as Any
    }
}