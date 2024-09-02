package com.example.ashraf_food.db

import androidx.room.TypeConverter

//we use the type converter as the api may have some data type like date which the room
// databse doesnot support so it may cause error , so we need to convert the data into the format that the
// room database support like into string or int

class converterOfType {
    @TypeConverter
    fun anyToString(attributes: Any): String {
        if (attributes == null) {
            return ""
        } else {
            return attributes as String
        }

    }


    @TypeConverter
    fun StringToAny(attributes: String): Any {
        return if (attributes == null)
           return ""
        else
            return attributes as Any
    }
}