package com.example.ashraf_food.Dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Cart_table")
data class CartMeal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val popular: popular?=null,
    val meal: Meal?=null,
    val plusData:Int,
    val minusData:Int,
    var quantity: Int,
    var totalPrice:Int
)
