package com.mishenka.notbasic.map

import android.util.Log
import androidx.lifecycle.LiveData
import com.mishenka.notbasic.data.model.photo.SearchCallback
import com.mishenka.notbasic.data.source.AppRepository
import com.mishenka.notbasic.util.PhotosSearchController
import com.mishenka.notbasic.util.SearchParams

class MapPhotosSearchController(
    private val appRepository: AppRepository,
    endlessPreferred: LiveData<Boolean>
) : PhotosSearchController(endlessPreferred) {

    override val TAG: String = "Map Search Controller"

    override fun getSearchResults(
        callback: SearchCallback,
        params: SearchParams,
        isContinuation: Boolean,
        page: Int
    ) {
        if (params is MapSearchParams) {
            appRepository.getMapSearchResults(params.lat, params.lng, callback, isContinuation, page)
        } else {
            Log.i("NYA", "(from $TAG) Invalid params type")
        }
    }

}