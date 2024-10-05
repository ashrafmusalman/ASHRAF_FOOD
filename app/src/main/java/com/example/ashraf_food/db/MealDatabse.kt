package com.example.ashraf_food.db

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.ashraf_food.Dataclass.Bottom
import com.example.ashraf_food.Dataclass.CartMeal
import com.example.ashraf_food.Dataclass.Category
import com.example.ashraf_food.Dataclass.Meal
import com.example.ashraf_food.Dataclass.popular

@Database(entities = [Meal::class, Bottom::class, Category::class, popular::class, CartMeal::class], version = 9)
@TypeConverters(converterOfType::class)//use only

abstract class MealDatabse : RoomDatabase() {
    abstract fun mealDao(): DAO_Interface//instance of the interface
    abstract fun cartMealDao(): CartMealDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabse? = null//  @Volatile:

        //Marks the INSTANCE field as volatile, ensuring that changes to this field are immediately visible to other threads. This is crucial for the singleton pattern to prevent multiple instances from being created.
        //synchronized:

        // Ensures that only one thread can execute the block of code that creates the database instance at a time, which is essential for thread safety when creating a singleton instance.
        @Synchronized//only one thread can use the database at a time
        fun getInstance(ctx: Context): MealDatabse {//we use the single ton as to ensure that only one isntance is created
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    ctx.applicationContext,
                    MealDatabse::class.java,
                    "mealDb"
                )
                    .fallbackToDestructiveMigration()//this means if the database is destroyed and and build again
                    // we want to keep our previous data present inside the database
                    .build()

            }
            return INSTANCE!!

        }


    }
}
