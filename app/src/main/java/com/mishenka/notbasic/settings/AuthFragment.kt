package com.mishenka.notbasic.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mishenka.notbasic.R
import com.mishenka.notbasic.databinding.FragmentAuthBinding
import com.mishenka.notbasic.util.obtainAuthVM


class AuthFragment : Fragment(), AuthCallback {

    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        (DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
                as FragmentAuthBinding)
            .apply {
                authVM = (activity as AppCompatActivity).obtainAuthVM()
                lifecycleOwner = activity
            }.also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBindings()
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.hide()

        super.onResume()
    }


    override fun onPause() {
        (activity as AppCompatActivity).supportActionBar?.show()

        super.onPause()
    }


    private fun setupBindings() {
        with(binding) {
            authVM?.loginError?.observe(this@AuthFragment, Observer {
                it.getContentIfNotHandled()?.let { error ->
                    authUsernameEt.error = error
                }
            })

            authLoginB.setOnClickListener {
                val username =  authUsernameEt.text.toString()
                authVM?.logInUser(username, context!!, this@AuthFragment)
            }

            authCreateB.setOnClickListener {
                val username = authUsernameEt.text.toString()
                authVM?.createUser(username, context!!, this@AuthFragment)
            }
        }
    }

    override fun onAuthenticationFinished() {
        (activity as AppCompatActivity).supportFragmentManager.popBackStack()
    }


    companion object {

        fun newInstance() = AuthFragment()

    }

}