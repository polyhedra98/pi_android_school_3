package com.mishenka.notbasic.favourites


import android.os.Bundle
import android.util.Log
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
import com.mishenka.notbasic.util.SwipeItemTouchHelperCallback
import com.mishenka.notbasic.util.obtainAuthVM
import com.mishenka.notbasic.util.obtainHomeVM

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

        with(binding) {

            authVM?.userId?.observe(this@FavouritesFragment, Observer { userId ->
                if (userId != null) {
                    homeVM?.let { safeHomeVM ->
                        safeHomeVM.getFavourites(userId)
                        if (favouritesRv.adapter == null) {
                            val adapter = FavouriteAdapter(userId, homeVM!!)
                            favouritesRv.adapter = adapter
                            ItemTouchHelper(SwipeItemTouchHelperCallback(adapter, homeVM!!))
                                .attachToRecyclerView(favouritesRv)
                            favouritesRv.layoutManager =
                                LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                        }
                        favouritesAuthErrorTv.visibility = View.GONE
                        favouritesRv.visibility = View.VISIBLE
                    }
                } else {
                    favouritesAuthErrorTv.visibility = View.VISIBLE
                    favouritesRv.visibility = View.GONE
                }
            })
        }
    }


    companion object {

        fun newInstance() = FavouritesFragment()

    }

}
