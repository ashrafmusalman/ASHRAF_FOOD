package com.example.ashraf_food.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.CartMeal

@Dao
interface CartMealDao {
    @Insert
    suspend fun insertCartMeal(cartMeal: CartMeal)

    @Update
    suspend fun updateCart(cartMeal: CartMeal)
    @Delete
    suspend fun delete(meal: CartMeal)

    @Query("SELECT * FROM Cart_table")
    suspend fun getAllCartMeals(): List<CartMeal>
}