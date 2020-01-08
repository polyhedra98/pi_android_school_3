package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.content.ContentType
import com.mishenka.notbasic.data.content.HistoryContentExtras
import com.mishenka.notbasic.data.content.HistoryContentResponse
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.data.model.user.HistorySelectItem
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.content.ContentManager
import com.mishenka.notbasic.utils.recycler.HistoryAdapter
import com.mishenka.notbasic.viewmodels.PrefVM
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HistoryFragment : Fragment() {

    private val TAG = "HistoryFragment"


    private val prefVM by sharedViewModel<PrefVM>()

    private val contentManager = get<ContentManager>()


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

        return inflater.inflate(R.layout.fragment_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
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
        history_upper_info_tv.text = getString(R.string.history_anonymous_ui)
        history_rv.visibility = View.INVISIBLE
    }


    private fun setupForUser(userId: Long, username: String) {
        Log.i("NYA_$TAG", "Setting up for user")
        history_upper_info_tv.text = getString(R.string.history_ui, username)
        history_rv.visibility = View.VISIBLE

        fetchHistory(userId)
    }


    private fun fetchHistory(userId: Long) {
        val observable = contentManager.requestContent(
            ContentType.HISTORY_TYPE,
            HistoryContentExtras(
                get(),
                userId
            )
        )

        observable.observe(this, Observer {
            (it as? HistoryContentResponse?)?.let { response ->
                setupRecycler(response.historyData)
            }
        })
    }


    private fun setupRecycler(items: List<HistorySelectItem?>) {
        history_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        history_rv.adapter = HistoryAdapter(items)
    }



    object HistoryRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "HISTORY_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_history_title

        override val shouldBeDisplayedAlone: Boolean
            get() = true

        override val isSecondary: Boolean
            get() = false

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = HistoryFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }
    }


}