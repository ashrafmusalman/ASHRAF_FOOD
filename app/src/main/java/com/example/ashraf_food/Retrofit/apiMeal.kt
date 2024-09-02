package com.example.ashraf_food.Retrofit


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object apiMeal {
    private const val BASE_URL="https://www.themealdb.com/api/json/v1/1/"



    val retrofit:Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    object mealClint{
        val apiservice:mealInterface by lazy {
            apiMeal.retrofit.create(mealInterface::class.java)
        }
    }
}