package com.example.ashraf_food.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular

@Dao
interface DAO_Interface {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insert(meal:Meal)

    //popualr
    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertPopular(meal:popular)

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertBottom(bottom: com.example.ashraf_food.Dataclass.Category)

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: com.example.ashraf_food.Dataclass.Bottom)


    @Update
    suspend fun update(meal:Meal)
    @Delete
    suspend fun delete(meal: Meal)

    @Delete
    suspend fun catdelete(meal: Category)

    @Delete
    suspend fun deletepopular(meal: popular)
    @Query("select * from DB_TABLE")
    fun getAllMeal():LiveData<List<Meal>>

    @Query("select * from Bottom")
    fun getAllBottom():LiveData<List<Bottom>>

    @Query("select * from popular")
    fun getAllPopular():LiveData<List<popular>>


    @Query("select * from Category WHERE strCategory = :strCategory")
    fun getCategory(strCategory:String):LiveData<List<Category>>



}