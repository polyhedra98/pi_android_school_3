package com.mishenka.notbasic.utils.workmanager

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.ApiService
import com.mishenka.notbasic.data.model.photo.SchedRes
import com.mishenka.notbasic.data.model.photo.network.OuterClass
import com.mishenka.notbasic.data.source.AppDatabase
import com.mishenka.notbasic.utils.date.DateConverter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.math.min


class ScheduleWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : Worker(appContext, workerParameters), KoinComponent {

    private val TAG = "ScheduleWorker"


    private val appDatabase by inject<AppDatabase>()


    override fun doWork(): Result {
        val plainQuery = inputData.getString(
            applicationContext.getString(R.string.worker_query_key)
        )
        val pages = inputData.getInt(
            applicationContext.getString(R.string.worker_pages_key), -1
        )

        Log.i("NYA_$TAG", "Set up worker with query $plainQuery, pages $pages")

        var fetchedPages = 0
        var firstUrl: String? = null
        var totalPhotos: Int = 0
        var totalPages: Int? = null

        if (plainQuery == null || pages == -1) {
            Log.i("NYA_$TAG", "Can't fetch data. Query / pages is null.")
            return Result.failure()
        }

        val query = plainQuery.replace(' ', '_')

        val baseUrl = "https://www.flickr.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)


        val initialLatch = CountDownLatch(1)
        GlobalScope.launch {
            appDatabase.photoDao().clearSchedResTable()

            val initialPage = 1
            handleNetworking(service, query, initialPage, object : Callback<OuterClass?> {
                override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                    Log.i("NYA_$TAG", "Networking failure for page $initialPage.")
                    initialLatch.countDown()
                }

                override fun onResponse(call: Call<OuterClass?>, response: Response<OuterClass?>) {
                    val body = response.body()
                    if (body != null) {
                        Log.i("NYA_$TAG", "Successful response for page $initialPage. Saving data.")
                        saveData(body) {
                            totalPages = body.photos?.pages
                            firstUrl = body.photos?.photo?.firstOrNull()?.constructURL()
                            body.photos?.photo?.let { safeList ->
                                totalPhotos += safeList.size
                            }
                            fetchedPages++
                            initialLatch.countDown()
                        }
                    } else {
                        Log.i("NYA_$TAG", "Response for page $initialPage is null.")
                        initialLatch.countDown()
                    }
                }
            })

        }

        Log.i("NYA_$TAG", "Waiting for initial latch.")
        initialLatch.await()
        Log.i("NYA_$TAG", "Initial latch barrier passed.")



        val totalPagesConstant = totalPages
        if (totalPagesConstant == null || totalPagesConstant < 2) {
            Log.i("NYA_$TAG", "Can't fetch further data, totalPages is null / < 2")
        } else {
            val min = min(totalPagesConstant, pages)
            val furtherLatch = CountDownLatch(min - 1)
            var pageCounter = 2
            while(pageCounter <= min) {
                val currentPage = pageCounter
                handleNetworking(service, query, currentPage, object : Callback<OuterClass?> {
                    override fun onFailure(call: Call<OuterClass?>, t: Throwable) {
                        Log.i("NYA_$TAG", "Networking failure for page $currentPage")
                        furtherLatch.countDown()
                    }

                    override fun onResponse(
                        call: Call<OuterClass?>,
                        response: Response<OuterClass?>
                    ) {
                        val body = response.body()
                        if (body != null) {
                            Log.i("NYA_$TAG", "Successful response for page $currentPage. Saving data.")
                            saveData(body) {
                                body.photos?.photo?.let { safeList ->
                                    totalPhotos += safeList.size
                                }
                                fetchedPages++
                                furtherLatch.countDown()
                            }
                        } else {
                            Log.i("NYA_$TAG", "Response for page $currentPage is null.")
                            furtherLatch.countDown()
                        }
                    }
                })
                pageCounter++
            }
            Log.i("NYA_$TAG", "Waiting for further latch.")
            furtherLatch.await()
            Log.i("NYA_$TAG", "Further latch barrier passed.")
        }


        saveValuesToPref(applicationContext, fetchedPages, DateConverter.fromDate(Date())!!)

        Log.i("NYA_$TAG", "Done working. Notification info: $firstUrl")
        if (firstUrl != null) {
            sendNotification(firstUrl!!, totalPhotos)
        } else {
            Log.i("NYA_$TAG", "Can't send notification, since first url is null.")
        }

        return Result.success()
    }



    private fun sendNotification(url: String, total: Int) {
        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.scheduler_notifications_channel_id)
        )
            .setSmallIcon(R.drawable.ic_search_24px)
            .setContentTitle(applicationContext.getString(R.string.scheduler_notification_title))
            .setContentText(applicationContext.getString(R.string.scheduler_notification_text, url))
            .setContentInfo(applicationContext.getString(R.string.scheduler_notification_info, total))

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.notify(
            applicationContext.getString(R.string.scheduler_notification_id).toInt(),
            builder.build()
        )
    }


    private fun handleNetworking(service: ApiService, query: String,
                                 page: Int, callback: Callback<OuterClass?>) {
        val apiKey: String = "d64c48cfef077371e18078e6e3657da5"

        val call = service.getSearchList(
            method = "flickr.photos.search",
            apiKey = apiKey,
            text = query,
            page = page
        )
        call.enqueue(callback)
    }


    private fun saveData(data: OuterClass, after: () -> Unit) {
        val photo = data.photos?.photo
        val page = data.photos?.page
        if (photo == null || photo.isEmpty() || page == null) {
            Log.i("NYA_$TAG", "Can't save data, photo list / page is null.")
            after.invoke()
            return
        }
        val listToSave = ArrayList<SchedRes>(photo.size)
        for (photoItem in photo) {
            listToSave.add(SchedRes(0, photoItem.constructURL(), page))
        }
        GlobalScope.launch {
            appDatabase.photoDao().insertAllScheduledResults(listToSave)
            after.invoke()
        }
    }


    private fun saveValuesToPref(context: Context, fetchedPages: Int, lastTime: Long) {
        with(context) {
            getSharedPreferences(
                getString(R.string.pref_filename), Context.MODE_PRIVATE
            ).edit().run {
                putInt(getString(R.string.pref_sched_fetched_pages_key), fetchedPages)
                putLong(getString(R.string.pref_sched_lasttime_key), lastTime)
                commit()
            }
        }
    }

}