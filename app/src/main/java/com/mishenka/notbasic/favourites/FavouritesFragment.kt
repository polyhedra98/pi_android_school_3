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
                homeVM = (activity as AppCompatActivity).obtainHomeVM().apply {
                    getFavourites((activity as AppCompatActivity).obtainAuthVM().userId.value)
                }
                authVM = (activity as AppCompatActivity).obtainAuthVM()

                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            authVM?.userId?.observe(this@FavouritesFragment, Observer { userId ->
                Log.i("NYA", "Observing userId $userId in FavouritesFragment")
                if (userId != null && homeVM != null && favouritesRv.adapter == null) {
                    val adapter = FavouriteAdapter(userId, homeVM!!)
                    favouritesRv.adapter = adapter
                    ItemTouchHelper(SwipeItemTouchHelperCallback(adapter, homeVM!!))
                        .attachToRecyclerView(favouritesRv)
                    favouritesRv.layoutManager =
                        LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                }
            })
        }
    }


    companion object {

        fun newInstance() = FavouritesFragment()

    }

}
