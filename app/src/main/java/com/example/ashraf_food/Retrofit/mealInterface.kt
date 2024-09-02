package com.example.ashraf_food.Retrofit

import com.example.ashraf_food.Dataclass.BottomList
import com.example.ashraf_food.Dataclass.CategoryList
import com.example.ashraf_food.Dataclass.mealList
import com.example.ashraf_food.Dataclass.popularList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface mealInterface {

    @GET("random.php")
    fun getRandomMeal():Call<mealList>


    @GET("filter.php")
    fun getPopularMeal(@Query("a") random:String):Call<popularList>


    @GET("categories.php")
    fun getBottomMeal():Call<BottomList>

    @GET("filter.php")
    fun getCategoryMeal(@Query("c") cat:String):Call<CategoryList>

    @GET("search.php")
    fun getSearchMeal(@Query("s") search:String):Call<mealList>

}