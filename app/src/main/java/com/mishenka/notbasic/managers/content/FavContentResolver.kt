package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.FavContentExtras
import com.mishenka.notbasic.data.content.FavContentResponse
import com.mishenka.notbasic.data.content.ItemType
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import com.mishenka.notbasic.utils.recycler.FavPagerElement
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
                val favouritesItemsInfo = emptyList<ItemType>().toMutableList()
                var previousCategory: String? = null

                for (favItem in favData) {
                    if (previousCategory != favItem.category) {
                        previousCategory = favItem.category
                        favouritesItemsList.add(previousCategory)
                        favouritesItemsInfo.add(ItemType.HEADER_TYPE)
                    }
                    favouritesItemsList.add(favItem.url)
                    favouritesItemsInfo.add(ItemType.PHOTO_TYPE)
                }

                val pagerData = ArrayList<FavPagerElement>(favouritesItemsList.size)
                for (i in 0 until favouritesItemsList.size) {
                    pagerData.add(object : FavPagerElement(
                        favouritesItemsList[i],
                        favouritesItemsInfo[i]
                    ) {})
                }


                MainScope().launch {

                    observable.value = FavContentResponse(
                        pagerData,
                        ceil(favDataCount.toDouble() / perPage.toDouble()).toInt()
                    )

                }

            }
        }

    }

}