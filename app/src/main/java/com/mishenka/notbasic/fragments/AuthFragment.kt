package com.mishenka.notbasic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mishenka.notbasic.R
import com.mishenka.notbasic.data.fragment.AuthFragmentData
import com.mishenka.notbasic.data.model.FragmentExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.managers.preservation.PreservationManager
import com.mishenka.notbasic.viewmodels.EventVM
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AuthFragment : Fragment() {

    private val TAG = "AuthFragment"


    private val eventVM by sharedViewModel<EventVM>()

    private val preservationManager = get<PreservationManager>()


    private var fragmentId: Long? = null

    private var restoredData: AuthFragmentData? = null

    private var usernameToPreserve: String? = null

    private var validationErrorToPreserve: String? = null


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
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoredData = (preservationManager.getDataForFragment(fragmentId!!) as? AuthFragmentData?)

        setupViews()
    }


    override fun onDestroyView() {
        preserveUsername()

        preserveValidationError()

        preservationManager.preserveFragmentData(fragmentId!!,
            AuthFragmentData(
                username = usernameToPreserve ?: restoredData?.username,
                validationError = validationErrorToPreserve
            )
        )

        super.onDestroyView()
    }


    private fun setupViews() {

        (restoredData?.username ?: usernameToPreserve)?.let { username ->
            auth_username_et.setText(username)
        }

        (restoredData?.validationError ?: validationErrorToPreserve)?.let { safeError ->
            handleValidationError(safeError)
        }

        auth_login_b.setOnClickListener {
            handleLogIn()
        }

        auth_create_b.setOnClickListener {
            handleCreation()
        }

    }


    private fun handleLogIn() {
        val username = auth_username_et.text.toString()

        val validationErrorMsg = validateCredentials(username)
        if (validationErrorMsg != null) {
            Log.i("NYA_$TAG", "Username $username validation failed. $validationErrorMsg")
            handleValidationError(validationErrorMsg)
            return
        }

        eventVM.logInCredentialsApproved(username)
    }


    private fun handleCreation() {
        //TODO("Implement.")
    }


    private fun handleValidationError(msg: String) {
        auth_username_et.error = msg
    }


    private fun validateCredentials(username: String): String? {
        return if (!username.matches(Regex("^[A-Za-z0-9_]*$"))) {
            getString(R.string.username_english_only_err)
        }
        else if (username.isBlank()) {
            getString(R.string.username_not_blank_err)
        }
        else {
            return null
        }
    }


    private fun preserveUsername() {
        usernameToPreserve = auth_username_et.text?.toString()
    }


    private fun preserveValidationError() {
        validationErrorToPreserve = auth_username_et.error?.toString()
    }


    object AuthRequest : IFragmentRequest {

        override val fragmentTag: String
            get() = "AUTH_FRAG"

        override val navigationTitleId: Int
            get() = R.string.nav_auth_title

        override val shouldBeDisplayedAlone: Boolean
            get() = false

        override val isSecondary: Boolean
            get() = true

        override val shouldHideToolbar: Boolean
            get() = false

        override fun instantiateFragment(context: Context, extras: FragmentExtras) = AuthFragment()
            .apply {
                arguments = Bundle().apply {
                    putLong(context.getString(R.string.bundle_fragment_id_key), extras.fragmentId)
                }
            }
    }

}