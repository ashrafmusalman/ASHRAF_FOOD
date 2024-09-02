package com.example.ashraf_food.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular

@Dao
interface BottomDAO {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insert(meal:Bottom)

    @Update
    suspend fun update(meal:Bottom)
    @Delete
    suspend fun delete(meal: Bottom)

    @Query("select * from bottom")
    fun getAllMeal():LiveData<List<Bottom>>
}