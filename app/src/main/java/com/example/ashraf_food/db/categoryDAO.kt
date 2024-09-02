package com.example.ashraf_food.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular

@Dao
interface categoryDAO {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insert(meal:Category)

    @Update
    suspend fun update(meal:Category)
    @Delete
    suspend fun delete(meal: Category)

    @Query("select * from category")
    fun getAllMeal():LiveData<List<Category>>
}