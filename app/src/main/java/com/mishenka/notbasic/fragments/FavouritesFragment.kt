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
import com.mishenka.notbasic.data.content.FavContentExtras
import com.mishenka.notbasic.data.content.FavContentResponse
import com.mishenka.notbasic.data.fragment.FavouritesFragmentData
import com.mishenka.notbasic.data.fragment.additional.DetailAdditionalExtras
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.interfaces.IPager
import com.mishenka.notbasic.interfaces.IPagerData
import com.mishenka.notbasic.interfaces.IPagerHost
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.utils.recycler.*
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.fragment_favourites.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


//TODO("Observe star / unstar events to dynamically change master / detail if needed.")
class FavouritesFragment : Fragment(), IPagerHost {

    private val TAG = "FavouritesFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val prefVM by sharedViewModel<PrefVM>()

    private val preservationManager = get<PreservationManager>()

    private val contentManager = get<ContentManager>()


    private var fragmentId: Long? = null

    private var restoredData: FavouritesFragmentData? = null

    private var userIdToPreserve: Long? = null

    private var pagerDataToPreserve: IPagerData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentId = arguments?.getLong(getString(R.string.bundle_fragment_id_key))

        if (fragmentId == null) {
            Log.i("NYA_$TAG", "Error. Fragment id is null.")
            throw Exception("Fragment id is null.")
        }

        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? FavouritesFragmentData?)

        if (prefVM.userId.value != restoredData?.userId) {
            restoredData = null
            preservationManager.clearDataForFragment(fragmentId!!)
        }

        setupViews()
    }


    override fun onDestroyView() {

        preservationManager.preserveFragmentData(fragmentId!!,
            FavouritesFragmentData(
                userId = userIdToPreserve ?: restoredData?.userId,
                pagerData = pagerDataToPreserve ?: restoredData?.pagerData
            ))

        super.onDestroyView()
    }


    override fun pagerDataChanged(newData: IPagerData) {
        Log.i("NYA_$TAG", "Pager data has changed.")
        pagerDataToPreserve = newData
    }


    override fun pageChangeRequested(newPage: Int) {
        Log.i("NYA_$TAG", "Page #$newPage requested.")
        prefVM.userId.value?.let {
            fetchFavourites(it, newPage)
        }
    }


    override fun pagerSetupRequested() {
        Log.i("NYA_$TAG", "Pager setup requested.")
        val pager = (childFragmentManager.findFragmentById(R.id.favourites_results_content_frame) as IPager)

        //TODO("It's really annoying that I have to do explicit cast, even though I inherit
        // PhotosAdapter in StdAdapter")
        pager.setupRecycler(
            FavAdapter(
                emptyList<FavPagerElement>(),
                this::handleResultClick,
                this::handleRemoval
            ) as ResponsiveHeaderlessAdapter<PhotosViewHolder>
        )

        val pagerData = pagerDataToPreserve ?: restoredData?.pagerData
        if (pagerData != null) {
            updatePagerData(pagerData, pager)
        } else {
            Log.i("NYA_$TAG", "No pager data to restore.")
        }
    }


    private fun setupViews() {

        prefVM.userId.observe(this, Observer {
            if (it == null) {
                setupForAnonymous()
            } else {
                val username = prefVM.username.value
                if (username == null) {
                    setupForAnonymous()
                } else {
                    setupForUser(it, username)
                }
            }
        })

    }


    private fun setupForAnonymous() {
        Log.i("NYA_$TAG", "Setting up for anonymous")
        favourites_upper_info_tv.text = getString(R.string.favourites_anonymous_ui)
        favourites_results_content_frame.visibility = View.INVISIBLE
    }


    private fun setupForUser(userId: Long, username: String) {
        Log.i("NYA_$TAG", "Setting up for user")

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? FavouritesFragmentData?)

        favourites_upper_info_tv.text = getString(R.string.favourites_ui, username)
        favourites_results_content_frame.visibility = View.VISIBLE

        initResultsFragment()

        if (restoredData == null) {
            fetchFavourites(userId)
        }
    }


    private fun initResultsFragment() {
        childFragmentManager.beginTransaction().run {
            replace(R.id.favourites_results_content_frame, ResultsFragment())
            commit()
        }
    }


    private fun preserveUserId(userId: Long) {
        userIdToPreserve = userId
    }


    private fun updatePagerData(data: IPagerData, pager: IPager) {
        pager.updateData(data)
    }


    private fun handleResultClick(url: String, category: String) {
        Log.i("NYA_$TAG", "Favourite $url from $category clicked.")
        eventVM.requestDetails(DetailAdditionalExtras(category, url))
    }


    private fun handleRemoval(url: String, category: String) {
        Log.i("NYA_$TAG", "Favourite $url from $category removal requested.")
        prefVM.userId.value?.let {
            prefVM.toggleStar(true, it, category, url, null, null)
        }
    }


    private fun fetchFavourites(userId: Long, argPage: Int? = null) {
        preserveUserId(userId)
        val page = argPage ?: 1

        val observable = contentManager.requestContent(
            ContentType.FAV_TYPE,
            FavContentExtras(
                get(),
                userId,
                page
            )
        )

        //TODO("Ok, I've just realized that I have to remove observer, once data is fetched. Memory leak!")
        observable.observe(this, Observer {
            (it as? FavContentResponse?)?.let { response ->

                val newData = object : IPagerData {
                    override val currentPage: Int = page
                    override val lastPage: Int = response.totalPages
                    override val pagerList: List<PagerElement> = response.favouriteItemsList
                }

                pagerDataChanged(newData)

                val pager = (childFragmentManager.findFragmentById(R.id.favourites_results_content_frame) as IPager)
                updatePagerData(newData, pager)
            }
        })
    }



    object FavouritesRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "FAV_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_fav_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = FavouritesFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }
    }

}