package com.mishenka.notbasic.data.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mishenka.notbasic.data.model.photo.PhotoDao
import com.mishenka.notbasic.data.model.photo.SchedRes
import com.mishenka.notbasic.data.model.user.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidContext()) }
}


@Database(
    entities = [
        User::class,
        History::class,
        Favourite::class,
        FavouriteSearch::class,
        FavouriteToSearchToUser::class,
        SchedRes::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun photoDao(): PhotoDao


    //TODO("I didn't want to add this, but the app crashed otherwise. Might have to figure out later.")
    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(AppDatabase::class.java) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also {
                    INSTANCE = it
                }
            }

    }
}