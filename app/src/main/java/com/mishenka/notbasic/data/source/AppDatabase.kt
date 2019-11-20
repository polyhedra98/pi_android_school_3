package com.mishenka.notbasic.data.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mishenka.notbasic.data.model.user.*

@Database(
    entities = [
        User::class,
        History::class,
        Favourite::class,
        FavouriteSearch::class,
        FavouriteToSearchToUser::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(AppDatabase::class.java) {
                INSTANCE ?:
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build().also {
                        INSTANCE = it
                    }
            }

    }
}