package com.mishenka.notbasic.favourites


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentFavouritesBinding
import com.mishenka.notbasic.util.*

class FavouritesFragment : Fragment() {


    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)
                as FragmentFavouritesBinding)
            .apply {
                homeVM = (activity as AppCompatActivity).obtainHomeVM()
                authVM = (activity as AppCompatActivity).obtainAuthVM()

                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(R.string.fav_nav_title)

        setupBindings()
    }


    //TODO("Fix unstar bug")
    private fun setupBindings() {
        with(binding) {
            authVM?.userId?.observe(this@FavouritesFragment, Observer { userId ->
                if (userId != null) {
                    homeVM?.let { safeHomeVM ->
                        favouritesAuthErrorTv.text = getString(R.string.fetching_favourites)
                        favouritesAuthErrorTv.visibility = View.VISIBLE
                        safeHomeVM.getFavourites(userId) {
                            setupRecyclerView()
                        }
                    }
                    homeVM?.apply {
                        favouriteItems.observe(this@FavouritesFragment, Observer {
                            observeResults(it, favouriteItemsInfo.value!!, favouritesRv)
                        })
                        requestedFavDismiss.observe(this@FavouritesFragment, Observer {
                            it.getContentIfNotHandled()?.let { pos ->
                                dismissFavourite(authVM!!.userId.value!!, pos)
                            }
                        })
                    }
                } else {
                    favouritesAuthErrorTv.text = getString(R.string.favourites_auth_error)
                    favouritesRv.visibility = View.GONE
                    favouritesAuthErrorTv.visibility = View.VISIBLE
                }
            })
        }
    }


    private fun observeResults(results: List<String>, additionalInfo: List<Int>, rv: RecyclerView) {
        (rv.adapter as FavouritesAdapter?)?.replaceFavItems(results, additionalInfo)
    }


    private fun setupRecyclerView() {
        with(binding) {
            if (homeVM!!.favouriteItems.value!!.isEmpty()) {
                favouritesAuthErrorTv.text = getString(R.string.empty_favourites)
                favouritesRv.visibility = View.GONE
                favouritesAuthErrorTv.visibility = View.VISIBLE
            } else {
                if (favouritesRv.adapter == null) {
                    val adapter = FavouritesAdapter(
                        homeVM!!.favouriteItems.value!!,
                        homeVM!!,
                        homeVM!!.favouriteItemsInfo.value!!
                    )
                    favouritesRv.adapter = adapter
                    ItemTouchHelper(SwipeItemTouchHelperCallback(FavouritesSwipeListener()))
                        .attachToRecyclerView(favouritesRv)

                    favouritesRv.layoutManager =
                        LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                }
                favouritesAuthErrorTv.visibility = View.GONE
                favouritesRv.visibility = View.VISIBLE
            }
        }
    }


    inner class FavouritesSwipeListener: SwipeListener {

        override fun onItemDismiss(position: Int) {
            (binding.favouritesRv.adapter as FavouritesAdapter?)?.removeFavItem(position)
        }
    }


    companion object {

        fun newInstance() = FavouritesFragment()

    }

}
