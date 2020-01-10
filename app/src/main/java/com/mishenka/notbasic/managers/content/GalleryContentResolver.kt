package com.mishenka.notbasic.managers.content

import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mishenka.notbasic.data.content.GalleryContentExtras
import com.mishenka.notbasic.data.content.GalleryContentResponse
import com.mishenka.notbasic.interfaces.IContentExtras
import com.mishenka.notbasic.interfaces.IContentResolver
import com.mishenka.notbasic.interfaces.IContentResponse
import kotlin.math.ceil


class GalleryContentResolver : IContentResolver {

    private val TAG = "GalContentResolver"


    override fun fetchData(observable: MutableLiveData<IContentResponse>, extras: IContentExtras) {

        val ext = (extras as GalleryContentExtras)
        val context = ext.context

        val cursor = context.contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER
            )

        if (cursor != null) {
            val page = ext.page
            //TODO("Change perPage")
            val perPage = 3
            val offset = (page - 1) * perPage
            var counter = 0

            val listToReturn = mutableListOf<String>()
            cursor.moveToFirst()
            cursor.move(offset)
            while (!cursor.isAfterLast && counter < perPage) {
                listToReturn.add(cursor.getString(
                    cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                ))
                cursor.moveToNext()
                counter++
            }
            cursor.moveToLast()
            val lastPage = ceil((cursor.position + 1).toDouble() / perPage.toDouble()).toInt()

            observable.value = GalleryContentResponse(
                listToReturn,
                ext.page,
                lastPage
            ).also {
                cursor.close()
            }
        }
        else {
            observable.value = null
        }

    }


}