package com.example.ashraf_food.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.BottomList
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.CategoryList
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.mealList
import com.example.ashraf_food.Dataclass.popular
import com.example.ashraf_food.Dataclass.popularList
import com.example.ashraf_food.Retrofit.apiMeal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class homeViewModel : ViewModel() {
    private var mutableMealCategory = MutableLiveData<Meal>()
    private var randomMutableMeal = MutableLiveData<List<popular>>()
    private var randomBottomMeal = MutableLiveData<List<Bottom>>()




    //this is for the random meal portion
    fun getMutableLiveMeal() {//this function will give us a random meal i.e. a mutableLiveMeal

        apiMeal.mealClint.apiservice.getRandomMeal().enqueue(object : Callback<mealList> {
            //api call
            override fun onResponse(call: Call<mealList>, response: Response<mealList>) {
                if (response.body() !== null) {//first check if the response we get is null or not if not null on success method is called
                    val randomMeal: Meal =
                        response.body()!!.meals[0]//DON'T FORGET TO PUT the BRACKET  and index 0 FOR THIS ARRAY BECAUSE THERE IS  A SINGLE IMAGE
                    // val randomMeal: Meal =response.body()!!.meals[0]  THIS LINE WILL GIVE US A RANDOMmEAL THROUGH THE API CALL
                    mutableMealCategory.value =randomMeal//this line set the random meal image to the mutablelivemeal
                    Log.d("MealImageURL", "Image URL: $randomMeal")


                } else {
                    return
                }


            }

            override fun onFailure(call: Call<mealList>, t: Throwable) {
                Log.d("HOME FRAGMENT", t.message.toString())
            }

        })
    }


    ///this is for the popular meal portion
    fun getMutableRandomLiveMeal() {//this function will give us a random meal i.e. a mutableLiveMeal

        apiMeal.mealClint.apiservice.getPopularMeal("Canadian")
            .enqueue(object : Callback<popularList> {
                override fun onResponse(call: Call<popularList>, response: Response<popularList>) {

                    if (response.body() !== null) {
                        val Respon = response.body()!!.meals
                        randomMutableMeal.value = Respon

                    }
                }

                override fun onFailure(call: Call<popularList>, t: Throwable) {
                }

            })

    }


    //for the BOTTOM MEAL

    fun getBottomMeal() {//this function will give us a random meal i.e. a mutableLiveMeal
        apiMeal.mealClint.apiservice.getBottomMeal()
            .enqueue(object : Callback<BottomList> {
                override fun onResponse(call: Call<BottomList>, response: Response<BottomList>) {
                    if (response.body() != null) {
                        val response = response.body()!!.categories
                        randomBottomMeal.value = response

                    }
                }

                override fun onFailure(call: Call<BottomList>, ty: Throwable) {

                }


            })

    }




//for the random meal
    fun observeMutableLiveMealData(): LiveData<Meal> {///this function will observe if the mutableLiveMeal is changed or not  and if changed it will return that mutableLieMeal and this return will be used into the homeFragment
        return mutableMealCategory//the LiveData is immutable that helps us to make sure that nobody can change the image from outside of the class
    }


    //for the popular meal
    fun observeRandomMutableLiveData(): LiveData<List<popular>> {
        return randomMutableMeal
    }


    //for the category meal
    fun observeBottomMeal(): LiveData<List<Bottom>> {
        return randomBottomMeal
    }



}