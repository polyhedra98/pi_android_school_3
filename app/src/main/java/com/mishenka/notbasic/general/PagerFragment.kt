package com.mishenka.notbasic.general

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.mishenka.notbasic.interfaces.IResponseData
import kotlinx.android.synthetic.main.partial_results.*

//TODO("Very 'suboptimal' implementation. Might want to change later, should be relatively easy.")
abstract class PagerFragment : Fragment() {

    var prevPageView: View? = null

    var nextPageView: View? = null


    abstract fun setupRecyclerView(observable: LiveData<IResponseData?>)

    abstract fun initPageViews()

    abstract fun setupPageViews()


    fun hidePageButtons() {
        prevPageView?.visibility = View.INVISIBLE
        nextPageView?.visibility = View.INVISIBLE
    }

    fun pageChanged(currentPage: Int?, lastPage: Int?) {
        if (currentPage == null || lastPage == null) {
            prevPageView?.visibility = View.INVISIBLE
            nextPageView?.visibility = View.INVISIBLE
        } else {
            if (currentPage > 1) {
                prevPageView?.visibility = View.VISIBLE
            } else {
                prevPageView?.visibility = View.INVISIBLE
            }
            if (currentPage in 1 until lastPage) {
                nextPageView?.visibility = View.VISIBLE
            } else {
                nextPageView?.visibility = View.INVISIBLE
            }
        }
    }

    fun downloadStatusChanged(finished: Boolean) {
        if (finished) {
            results_pb.visibility = View.INVISIBLE
            results_rv.visibility = View.VISIBLE
        } else {
            results_pb.visibility = View.VISIBLE
            results_rv.visibility = View.INVISIBLE
        }
    }

}