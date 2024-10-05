package com.example.ashraf_food.db

import androidx.room.TypeConverter
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class converterOfType {

    private val gson = Gson()

    // Convert a popular object to a JSON string
    @TypeConverter
    fun popularToString(popular: popular?): String {
        return if (popular == null) {
            ""
        } else {
            gson.toJson(popular)
        }
    }

    // Convert a JSON string back to a popular object
    @TypeConverter
    fun stringToPopular(data: String?): popular? {
        return if (data.isNullOrEmpty()) {
            null
        } else {
            try {
                gson.fromJson(data, popular::class.java)
            } catch (e: JsonSyntaxException) {
                null // Handle JSON parsing error
            }
        }
    }

    // Convert a Meal object to a JSON string
    @TypeConverter
    fun mealToString(meal: Meal?): String {
        return if (meal == null) {
            ""
        } else {
            gson.toJson(meal)
        }
    }

    // Convert a JSON string back to a Meal object
    @TypeConverter
    fun stringToMeal(data: String?): Meal? {
        return if (data.isNullOrEmpty()) {
            null
        } else {
            try {
                gson.fromJson(data, Meal::class.java)
            } catch (e: JsonSyntaxException) {
                null // Handle JSON parsing error
            }
        }
    }

    // If you still want to keep a generic to String method
    @TypeConverter
    fun anyToString(attributes: Any?): String {
        return if (attributes == null) {
            ""
        } else {
            gson.toJson(attributes) // Convert the object to JSON string
        }
    }

    // If you want to keep a generic from String method
    @TypeConverter
    fun stringToAny(attributes: String?): Any? {
        return if (attributes.isNullOrEmpty()) {
            null
        } else {
            try {
                // Attempt to determine the type based on the string
                // For example, use a specific identifier in your JSON to differentiate types
                val typeCheck = gson.fromJson(attributes, Any::class.java)
                when (typeCheck) {
                    is Meal -> stringToMeal(attributes)
                    is popular -> stringToPopular(attributes)
                    else -> null
                }
            } catch (e: JsonSyntaxException) {
                null // Handle parsing error
            }
        }
    }
}
