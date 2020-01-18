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
import com.mishenka.notbasic.data.content.SchedContentExtras
import com.mishenka.notbasic.data.content.SchedContentResponse
import com.mishenka.notbasic.data.fragment.SchedResFragmentData
import com.mishenka.notbasic.data.fragment.additional.DetailAdditionalExtras
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.interfaces.IPager
import com.mishenka.notbasic.interfaces.IPagerData
import com.mishenka.notbasic.interfaces.IPagerHost
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.utils.date.DateConverter
import com.mishenka.notbasic.utils.recycler.PagerElement
import com.mishenka.notbasic.utils.recycler.PhotosViewHolder
import com.mishenka.notbasic.utils.recycler.ResponsiveAdapter
import com.mishenka.notbasic.utils.recycler.StdAdapter
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.fragment_sched_res.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SchedResFragment : Fragment(), IPagerHost {

    private val TAG = "SchedResFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val prefVM by sharedViewModel<PrefVM>()

    private val preservationManager = get<PreservationManager>()

    private val contentManager = get<ContentManager>()


    private var fragmentId: Long? = null

    private var restoredData: SchedResFragmentData? = null

    private var queryToPreserve: String? = null

    private var startTimeToPreserve: Long? = null

    private var lastTimeToPreserve: Long? = null

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

        return inflater.inflate(R.layout.fragment_sched_res, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? SchedResFragmentData?)

        if (restoredData?.pagerData == null) {
            fetchData()
        }

        setupViews()
    }


    override fun onDestroyView() {
        preservationManager.preserveFragmentData(fragmentId!!,
            SchedResFragmentData(
                query = queryToPreserve,
                startTime = startTimeToPreserve,
                lastTime = lastTimeToPreserve,
                pagerData = pagerDataToPreserve
            ))

        super.onDestroyView()
    }


    override fun pagerDataChanged(newData: IPagerData) {
        Log.i("NYA_$TAG", "Pager dat ahas changed.")
        pagerDataToPreserve = newData
    }


    override fun pageChangeRequested(newPage: Int) {
        Log.i("NYA_$TAG", "Page #$newPage requested")
        fetchData(newPage)
    }


    override fun pagerSetupRequested() {
        Log.i("NYA_$TAG", "Pager setup requested.")
        val pager = (childFragmentManager.findFragmentById(R.id.sched_res_content_frame) as IPager?)

        //TODO("It's really annoying that I have to do explicit cast, even though I inherit
        // PhotosAdapter in StdAdapter")
        pager?.setupRecycler(
            StdAdapter(
                listOf(object : PagerElement(getString(R.string.default_sched_res_header)) {}),
                this::resultClicked,
                this::resultRemoved
            ) as ResponsiveAdapter<PhotosViewHolder>
        )

        val pagerData = pagerDataToPreserve ?: restoredData?.pagerData
        if (pagerData != null) {
            updatePagerData(pagerData, pager)
        } else {
            Log.i("NYA_$TAG", "No pager data to preserve.")
        }
    }


    private fun resultClicked(pagerElement: PagerElement) {
        Log.i("NYA_$TAG", "Scheduled result ${pagerElement.value} clicked")
        eventVM.requestDetails(DetailAdditionalExtras(
            category = queryToPreserve ?: restoredData?.query,
            url = pagerElement.value
        ))
    }


    private fun resultRemoved(pagerElement: PagerElement) {
        Log.i("NYA_$TAG", "Removing ${pagerElement.value}")
        prefVM.deleteScheduledResult(pagerElement.value,
            (pagerDataToPreserve?.currentPage ?: restoredData?.pagerData?.currentPage)!!)
    }



    private fun setupViews() {
        val data = prefVM.prefGetSchedulerData(context!!)

        if (data == null) {
            sched_res_upper_info_tv.text = getString(R.string.sched_res_not_set_up_ui)
        } else {
            if (!validateData(data)) { invalidateData() }

            preserveQuery(data.query)
            preserveStartTime(data.startTime)
            preserveLastTime(data.lastTime)

            val pages = resources.getStringArray(R.array.pages_schedule_entries).elementAt(
                resources.getIntArray(R.array.pages_scheduler_values).indexOf(data.pages)
            )
            val interval = resources.getStringArray(R.array.period_schedule_entries).elementAt(
                resources.getIntArray(R.array.period_schedule_values).indexOf(data.interval)
            )
            if (data.lastTime == (-1).toLong()) {
                sched_res_upper_info_tv.text = getString(R.string.sched_res_not_updated_ui,
                    DateConverter.toDate(data.startTime)?.toString(), data.query, pages, interval)
            } else {
                sched_res_upper_info_tv.text = getString(R.string.sched_res_updated_ui,
                    DateConverter.toDate(data.startTime)?.toString(), DateConverter.toDate(data.lastTime)?.toString(),
                    data.query, data.fetchedPages, data.pages, interval)
            }
        }

        initResultsFragment()
    }


    private fun validateData(data: PrefVM.PrefSchedulerData): Boolean {
        val prevStartTime = restoredData?.startTime
        if (prevStartTime != null && data.startTime != prevStartTime) {
            Log.i("NYA_$TAG", "Invalid start time.")
            return false
        }

        val prevLastTime = restoredData?.lastTime
        if (prevLastTime != null && data.lastTime != prevLastTime) {
            Log.i("NYA_$TAG", "Invalid last time.")
            return false
        }

        return true
    }


    private fun initResultsFragment() {
        childFragmentManager.beginTransaction().run {
            replace(R.id.sched_res_content_frame, ResultsFragment())
            commit()
        }
    }


    private fun updatePagerData(data: IPagerData, pager: IPager?) {
        with(data) {
            pager?.updateHeader(
                object : PagerElement(getString(R.string.sched_res_header,
                    data.currentPage, data.lastPage)) {}
            )
            pager?.updateData(data)
        }
    }


    private fun preserveQuery(query: String) {
        queryToPreserve = query
    }


    private fun preserveStartTime(startTime: Long) {
        startTimeToPreserve = startTime
    }


    private fun preserveLastTime(lastTime: Long?) {
        lastTimeToPreserve = lastTime
    }


    private fun invalidateData() {
        restoredData = null
        queryToPreserve = null
        startTimeToPreserve = null
        lastTimeToPreserve = null
        pagerDataToPreserve = null
    }


    private fun fetchData(argPage: Int? = null) {
        val page = argPage ?: 1

        val observable = contentManager.requestContent(
            ContentType.SCHED_TYPE,
            SchedContentExtras(get(), page)
        )

        //TODO("Ok, I've just realized that I have to remove observer, once data is fetched. Memory leak!")
        observable.observe(this, Observer {
            (it as? SchedContentResponse?)?.let { response ->

                val pagerList = ArrayList<PagerElement>(response.schedItemsList.size)
                for (schedItem in response.schedItemsList) {
                    pagerList.add(object : PagerElement(schedItem.url) {})
                }

                val newData = object : IPagerData {
                    override val currentPage: Int = page
                    override val lastPage: Int = response.totalPages
                    override val pagerList: List<PagerElement> = pagerList
                }

                pagerDataChanged(newData)

                val pager = (childFragmentManager.findFragmentById(R.id.sched_res_content_frame) as IPager?)
                updatePagerData(newData, pager)

            }
        })
    }



    object SchedResRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "SCHED_RES_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_sched_res_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = SchedResFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }

    }

}