package com.mishenka.notbasic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.interfaces.IFragmentAdditionalExtras
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.utils.Event
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val eventsModule = module {
    viewModel { EventVM() }
}

class EventVM: ViewModel() {

    private val _fragmentRequested =
        MutableLiveData<Event<Pair<IFragmentRequest, IFragmentAdditionalExtras?>>>()
    val fragmentRequested: LiveData<Event<Pair<IFragmentRequest, IFragmentAdditionalExtras?>>>
        get() = _fragmentRequested


    private val _secondaryFragmentsRemovalRequested =
        MutableLiveData<Event<String>>()
    val secondaryFragmentsRemovalRequested: LiveData<Event<String>>
        get() = _secondaryFragmentsRemovalRequested


    private val _keyboardHideRequested = MutableLiveData<Event<Unit>>()
    val keyboardHideRequested: LiveData<Event<Unit>>
        get() = _keyboardHideRequested


    private val _detailsRequested = MutableLiveData<Event<IFragmentAdditionalExtras>>()
    val detailsRequested: LiveData<Event<IFragmentAdditionalExtras>>
        get() = _detailsRequested


    private val _loginCredentialsApproved = MutableLiveData<Event<String>>()
    val loginCredentialsApproved: LiveData<Event<String>>
        get() = _loginCredentialsApproved


    private val _signUpCredentialsApproved = MutableLiveData<Event<String>>()
    val signUpCredentialsApproved: LiveData<Event<String>>
        get() = _signUpCredentialsApproved



    fun requestFragment(request: IFragmentRequest, additionalExtras: IFragmentAdditionalExtras?) {
        _fragmentRequested.value = Event(Pair(request, additionalExtras))
    }


    fun requestSecondaryFragmentsRemoval(fragmentTag: String) {
        _secondaryFragmentsRemovalRequested.value = Event(fragmentTag)
    }


    fun requestKeyboardHide() {
        _keyboardHideRequested.value = Event(Unit)
    }


    fun requestDetails(additionalExtras: IFragmentAdditionalExtras) {
        _detailsRequested.value = Event(additionalExtras)
    }


    fun logInCredentialsApproved(username: String) {
        _loginCredentialsApproved.value = Event(username)
    }


    fun signUpCredentialsApproved(username: String) {
        _signUpCredentialsApproved.value = Event(username)
    }


}