package com.example.ashraf_food.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.CategoryList
import com.example.ashraf_food.Retrofit.apiMeal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class category_clk : ViewModel() {
    private var CategoryMeal = MutableLiveData<List<Category>>()

    private val _searchMeal = MutableLiveData<List<Category>?>()
    val searchMeal: MutableLiveData<List<Category>?> get() = _searchMeal

    //for the category meal when it is clicked and move to next fragment
    fun getCategoryMEAL(categoryname: String) {//this function will give us a random meal i.e. a mutableLiveMeal
        apiMeal.mealClint.apiservice.getCategoryMeal(categoryname)
            .enqueue(object : Callback<CategoryList> {
                override fun onResponse(
                    call: Call<CategoryList>,
                    response: Response<CategoryList>
                ) {
                    if (response.body() != null) {
                        val responseMeals = response.body()!!.meals
                        val categorizedMeals = responseMeals.map {
                            it.copy(strCategory = categoryname)
                        }
                        CategoryMeal.value = categorizedMeals
                        Log.d("response1", categorizedMeals.toString())
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                    Log.d(t.message.toString(), "categoryClick: ")
                }


            })

    }


    //for the category meal when it is clicked and moved to next fragment
    fun ObserveCategoryMEAL(): LiveData<List<Category>> {
        return CategoryMeal
    }


}