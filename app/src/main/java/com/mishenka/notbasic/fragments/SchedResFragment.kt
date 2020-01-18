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
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.utils.date.DateConverter
import com.mishenka.notbasic.viewmodels.EventVM
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.fragment_sched_res.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SchedResFragment : Fragment() {

    private val TAG = "SchedResFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val prefVM by sharedViewModel<PrefVM>()


    private var fragmentId: Long? = null


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

        eventVM.schedulerValuesUpdated.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                setupViews()
            }
        })

        setupViews()
    }



    private fun setupViews() {
        val data = prefVM.prefGetSchedulerData(context!!)

        if (data == null) {
            sched_res_upper_info_tv.text = getString(R.string.sched_res_not_set_up_ui)
        } else {
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