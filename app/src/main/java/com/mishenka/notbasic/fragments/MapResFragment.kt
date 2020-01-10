package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.ContentType
import com.mishenka.notbasic.data.content.LatLngContentExtras
import com.mishenka.notbasic.data.content.LatLngContentResponse
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.data.pager.LatLngPagerData
import com.mishenka.notbasic.data.fragment.additional.MapResAdditionalExtras
import com.mishenka.notbasic.data.fragment.MapResFragmentData
import com.mishenka.notbasic.data.fragment.additional.DetailAdditionalExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.interfaces.IPager
import com.mishenka.notbasic.interfaces.IPagerData
import com.mishenka.notbasic.interfaces.IPagerHost
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.utils.recycler.PagerElement
import com.mishenka.notbasic.utils.recycler.StdAdapter
import com.mishenka.notbasic.utils.recycler.PhotosViewHolder
import com.mishenka.notbasic.utils.recycler.ResponsiveAdapter
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapResFragment : Fragment(), IPagerHost {

    private val TAG = "MapResFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val prefVM by sharedViewModel<PrefVM>()

    private val preservationManager = get<PreservationManager>()

    private val contentManager = get<ContentManager>()


    private var fragmentId: Long? = null

    private var restoredData: MapResFragmentData? = null

    private var lat: Double? = null

    private var lng: Double? = null

    private var pagerDataToPreserve: LatLngPagerData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))
        lat = arguments?.getDouble(getString(R.string.bundle_lat_key))
        lng = arguments?.getDouble(getString(R.string.bundle_lng_key))

        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }
        if (lat == null || lng == null) {
            Log.i("NYA_$TAG", "Error. Lat / Lng is null.")
            throw Exception("Lat / Lng is null.")
        } else {
            Log.i("NYA_$TAG", "Instantiated with lat: $lat, lng: $lng")
        }
        return inflater.inflate(R.layout.fragment_map_res, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? MapResFragmentData?)

        if (restoredData == null) {
            handleSearch(1)
        }

        setupViews()
    }


    override fun onDestroyView() {
        preservationManager.preserveFragmentData(fragmentId!!,
            MapResFragmentData(
                pagerData = pagerDataToPreserve ?: restoredData?.pagerData
            )
        )

        super.onDestroyView()
    }


    override fun pagerDataChanged(newData: IPagerData) {
        Log.i("NYA_$TAG", "Pager data has changed.")
        pagerDataToPreserve = (newData as? LatLngPagerData)
    }


    override fun pageChangeRequested(newPage: Int) {
        Log.i("NYA_$TAG", "Page #$newPage requested.")
        handleSearch(newPage)
    }


    override fun pagerSetupRequested() {
        Log.i("NYA_$TAG", "Pager setup requested.")
        val pager = (childFragmentManager.findFragmentById(R.id.map_res_results_content_frame) as IPager)

        //TODO("It's really annoying that I have to do explicit cast, even though I inherit
        // PhotosAdapter in StdAdapter")
        pager.setupRecycler(
            StdAdapter(
                listOf(object : PagerElement(getString(R.string.default_map_res_header)) {}),
                this::mapResResultClicked,
                this::mapResResultRemoved
            ) as ResponsiveAdapter<PhotosViewHolder>
        )

        val pagerData = pagerDataToPreserve ?: restoredData?.pagerData
        if (pagerData != null) {
            updatePagerData(pagerData, pager)
        } else {
            Log.i("NYA_$TAG", "No pager data to restore.")
        }
    }


    private fun mapResResultClicked(pagerElement: PagerElement) {
        Log.i("NYA_$TAG", "MapRes result ${pagerElement.value} clicked")
        val pagerData = (pagerDataToPreserve ?: restoredData?.pagerData)!!
        eventVM.requestDetails(
            DetailAdditionalExtras(
                category = getString(R.string.lat_lng_string_representation, pagerData.lat, pagerData.lng),
                url = pagerElement.value
            )
        )
    }


    private fun mapResResultRemoved(pagerElement: PagerElement) {
        TODO("Implement.")
    }


    private fun initResultsFragment() {
        childFragmentManager.beginTransaction().run {
            replace(R.id.map_res_results_content_frame, ResultsFragment())
            commit()
        }
    }


    private fun setupViews() {

        initResultsFragment()

    }


    private fun handleSearch(argPage: Int? = null) {
        //TODO("I could use class values, but any potential future changes would be more troublesome")
        val localLat = (pagerDataToPreserve?.lat ?: restoredData?.pagerData?.lat ?: lat)!!
        val localLng = (pagerDataToPreserve?.lng ?: restoredData?.pagerData?.lng ?: lng)!!
        val page = argPage ?: 1

        prefVM.conditionallySaveSearch(
            getString(R.string.lat_lng_string_representation, localLat, localLng),
            page
        )

        val observable = contentManager.requestContent(
            ContentType.LAT_LNG_TYPE,
            LatLngContentExtras(localLat, localLng, page)
        )

        //TODO("Ok, I've just realized that I have to remove observer, once data is fetched. Memory leak!")
        observable.observe(this, Observer {
            (it as? LatLngContentResponse?)?.let { response ->
                conditionallyUpdateData(response)
            }
        })
    }


    private fun conditionallyUpdateData(response: LatLngContentResponse) {

        val photos = response.response.photos
        if (photos != null) {

            val currentPage = photos.page
            val lastPage = photos.pages
            val photo = photos.photo?.map { photo -> photo.constructURL() }

            if (currentPage != null && lastPage != null && photo != null) {

                val photoForPager = ArrayList<PagerElement>(photo.size)
                //TODO("There has to be some obscure syntax to avoid this.")
                for (photoItem in photo.toList()) {
                    photoForPager.add(object : PagerElement(photoItem) {})
                }

                val newData = object : LatLngPagerData() {
                    override val lat: Double = response.lat
                    override val lng: Double = response.lng
                    override val currentPage: Int = currentPage
                    override val lastPage: Int = lastPage
                    override val pagerList: List<PagerElement> = photoForPager
                }

                pagerDataChanged(newData)

                val pager = (childFragmentManager.findFragmentById(R.id.map_res_results_content_frame) as IPager)
                updatePagerData(newData, pager)

            } else {
                Log.i("NYA_$TAG", "One of the important LatLngContentResponse " +
                        "elements is null. No data change notification.")
            }

        } else {
            Log.i("NYA_$TAG", "LatLngContentResponse Photos class is null")
        }

    }


    private fun updatePagerData(data: LatLngPagerData, pager: IPager) {
        with(data) {
            pager.updateHeader(
                object : PagerElement(getString(R.string.map_res_header, lat, lng, currentPage, lastPage)) {}
            )
        }
        pager.updateData(data)
    }



    object MapResRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "MAP_RES_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_map_res_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = MapResFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)

                    (extras.additionalExtras as MapResAdditionalExtras).run {
                        putDouble(context.getString(R.string.bundle_lat_key), lat)
                        putDouble(context.getString(R.string.bundle_lng_key), lng)
                    }
                }
            }

    }
}