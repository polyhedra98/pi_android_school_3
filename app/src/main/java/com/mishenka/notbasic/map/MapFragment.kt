package com.mishenka.notbasic.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentMapBinding
import com.mishenka.notbasic.util.obtainHomeVM

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
                as FragmentMapBinding)
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
            homeVM?.let { safeHomeVM ->
                mapSearchB.setOnClickListener {
                    safeHomeVM.onMapSearchClicked("Default location")
                }
            }
        }
    }


    companion object {

        fun newInstance() = MapFragment()

    }

}