package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.FavContentExtras
import com.mishenka.notbasic.data.content.FavContentResponse
import com.mishenka.notbasic.data.content.FavItemType
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.ceil


class FavContentResolver : IContentResolver {

    private val TAG = "FavContentResolver"


    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {

        val ext = (extras as FavContentExtras)
        val appDatabase = ext.appDatabase

        //TODO("Change perPage")
        val perPage = 3
        val offset = (ext.page - 1) * perPage

        //TODO("Change scope")
        GlobalScope.launch {
            val favData = appDatabase.userDao().
                getCategoriesAndFavsForUser(ext.userId, perPage, offset)
            val favDataCount = appDatabase.userDao().
                getCategoriesAndFavsCountForUser(ext.userId)
            if (favData == null || favDataCount == null) {
                MainScope().launch {
                    Log.i("NYA_$TAG", "Error fetching data. FavData / FavDataCount is null.")
                    observable.value = null
                }
            }
            else {

                val favouritesItemsList = emptyList<String>().toMutableList()
                val favouritesItemsInfo = emptyList<FavItemType>().toMutableList()
                var previousCategory: String? = null

                for (favItem in favData) {
                    if (previousCategory != favItem.category) {
                        previousCategory = favItem.category
                        favouritesItemsList.add(previousCategory)
                        favouritesItemsInfo.add(FavItemType.CATEGORY_TYPE)
                    }
                    favouritesItemsList.add(favItem.url)
                    favouritesItemsInfo.add(FavItemType.PHOTO_TYPE)
                }

                MainScope().launch {

                    observable.value = FavContentResponse(
                        favouritesItemsList,
                        favouritesItemsInfo,
                        ceil(favDataCount.toDouble() / perPage.toDouble()).toInt()
                    )

                }

            }
        }

    }

}