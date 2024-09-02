package com.example.ashraf_food.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular

@Dao
interface popularDAO {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insert(meal:popular)

    @Update
    suspend fun update(meal:popular)
    @Delete
    suspend fun delete(meal: popular)

    @Query("select * from popular")
    fun getAllMeal():LiveData<List<popular>>
}