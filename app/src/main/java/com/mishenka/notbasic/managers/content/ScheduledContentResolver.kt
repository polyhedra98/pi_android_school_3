package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.SchedContentExtras
import com.mishenka.notbasic.data.content.SchedContentResponse
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ScheduledContentResolver : IContentResolver {

    private val TAG = "SchdContentResolver"


    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {

        val ext = (extras as SchedContentExtras)
        val appDatabase = ext.appDatabase
        val page = ext.page

        //TODO("Change scope")
        GlobalScope.launch {
            val schedData = appDatabase.photoDao().getScheduledResultsForPage(page)
            val lastPage = appDatabase.photoDao().getLastSchedResPage()

            if (schedData == null || lastPage == null) {
                MainScope().launch {
                    Log.i("NYA_$TAG", "Error fetching data. SchedData / LastPage is null.")
                    observable.value = null
                }
            } else {
                MainScope().launch {
                    observable.value = SchedContentResponse(
                        schedData,
                        lastPage
                    )
                }
            }
        }

    }

}