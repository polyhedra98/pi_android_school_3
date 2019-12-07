package com.mishenka.notbasic.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentGalleryBinding
import com.mishenka.notbasic.util.SwipeItemTouchHelperCallback
import com.mishenka.notbasic.util.SwipeListener
import com.mishenka.notbasic.util.obtainHomeVM


class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
                as FragmentGalleryBinding)
            .apply {
                homeVM = (activity as AppCompatActivity).obtainHomeVM()
                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBindings()
    }


    private fun setupBindings() {
        with(binding) {
            homeVM?.apply {
                setupRecyclerView()

                galleryResultsField.observe(this@GalleryFragment, Observer {
                    observeHeader(it, galleryRv)
                })
                galleryResultsList.observe(this@GalleryFragment, Observer {
                    observeResults(it, galleryRv)
                })
            }
        }
    }


    private fun observeHeader(header: String, rv: RecyclerView) {
        (rv.adapter as GalleryAdapter?)?.replaceHeader(header)
    }


    private fun observeResults(results: List<String>, rv: RecyclerView) {
        (rv.adapter as GalleryAdapter?)?.replaceItems(results)
    }


    private fun setupRecyclerView() {
        with(binding) {
            val layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)

            galleryRv.layoutManager = layoutManager
            ItemTouchHelper(SwipeItemTouchHelperCallback(GallerySwipeListener()))
                .attachToRecyclerView(galleryRv)

            val items = if (!homeVM!!.galleryResultsList.value.isNullOrEmpty()) {
                homeVM!!.galleryResultsList.value!!
            } else {
                listOf(homeVM!!.galleryResultsField.value!!)
            }
            galleryRv.adapter = GalleryAdapter(items, homeVM!!)
        }
    }


    inner class GallerySwipeListener: SwipeListener {

        override fun onItemDismiss(position: Int) {
            (binding.galleryRv.adapter as GalleryAdapter?)?.removeGalItem(position)
        }
    }

    companion object {

        fun newInstance() = GalleryFragment()

    }

}