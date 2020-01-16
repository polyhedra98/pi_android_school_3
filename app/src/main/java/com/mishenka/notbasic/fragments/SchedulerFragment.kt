package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.fragment.SchedulerFragmentData
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SchedulerFragment : Fragment() {

    private val TAG = "SchedulerFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val preservationManager = get<PreservationManager>()


    private var fragmentId: Long? = null

    private var restoredData: SchedulerFragmentData? = null

    private var searchFieldToPreserve: String? = null

    private var validationErrorToPreserve: String? = null

    private var spinnerValueToPreserve: Any? = null


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
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? SchedulerFragmentData?)

        setupViews()
    }


    override fun onDestroyView() {
        preserveSearchField()

        preserveValidationError()

        preserveSpinnerValue()

        preservationManager.preserveFragmentData(fragmentId!!,
            SchedulerFragmentData(
                searchField = searchFieldToPreserve,
                validationError = validationErrorToPreserve,
                spinnerValue = spinnerValueToPreserve
            )
        )

        super.onDestroyView()
    }



    private fun setupViews() {

        scheduler_spinner.adapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.update_schedule_entries,
            android.R.layout.simple_spinner_item
        )


        scheduler_submit_b.setOnClickListener {
            handleSubmit()
        }

    }


    private fun handleSubmit() {
        val query = scheduler_search_et.text.toString()
        val validationErrorMsg = validateQuery(query)
        if (validationErrorMsg != null) {
            Log.i("NYA_$TAG", "Query $query validation failed. $validationErrorMsg")
            handleValidationError(validationErrorMsg)
            return
        }

        val interval = scheduler_spinner.selectedItem.toString()

        Log.i("NYA_$TAG", "Setting up manager for query $query, interval $interval")

        //TODO("Setup WorkManager")

        eventVM.schedulerValuesApproved()
    }


    private fun validateQuery(query: String): String? {
        return if (!query.matches(Regex("^[A-Za-z0-9_ ]*$"))) {
            getString(R.string.query_english_only_err)
        }
        else if (query.isBlank()) {
            getString(R.string.query_not_blank_err)
        }
        else {
            return null
        }
    }


    private fun handleValidationError(msg: String) {
        scheduler_search_et.error = msg
    }


    private fun preserveSearchField() {
        searchFieldToPreserve = scheduler_search_et.text.toString()
    }


    private fun preserveValidationError() {
        validationErrorToPreserve = scheduler_search_et.error?.toString()
    }


    private fun preserveSpinnerValue() {
        spinnerValueToPreserve = scheduler_spinner.selectedItem.toString()
    }



    object SchedulerRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "SCHED_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_scheduler_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = true

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = SchedulerFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }

    }

}