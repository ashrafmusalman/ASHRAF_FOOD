package com.example.ashraf_food.Dataclass

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
@Entity(tableName = "popular")
@Parcelize

data class popular(
    @PrimaryKey
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
):Parcelable