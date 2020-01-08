package com.mishenka.notbasic.managers.content

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.HistoryContentExtras
import com.mishenka.notbasic.data.content.HistoryContentResponse
import com.mishenka.notbasic.data.source.AppDatabase
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.dsl.module


//TODO("Passing database reference in extras for now, because I can't inject it (don't yet know why).
// Might have to figure it out later. Part 250")
class HistoryContentResolver : IContentResolver {

    private val TAG = "HistContentResolver"


    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {

        val ext = (extras as HistoryContentExtras)
        val appDatabase = ext.appDatabase

        //TODO("Change scope")
        GlobalScope.launch {
            val historyData = appDatabase.userDao().
                getUserHistory(ext.userId, ext.limit, ext.offset)

            MainScope().launch {

                if (historyData == null) {
                    Log.i("NYA_$TAG", "Error fetching data. HistoryData is null")
                    //TODO("Change to 'proper' error return")
                    observable.value = null
                }
                else {
                    observable.value = HistoryContentResponse(historyData)
                }

            }
        }

    }

}