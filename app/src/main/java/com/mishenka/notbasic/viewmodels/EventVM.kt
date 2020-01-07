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


    private val _keyboardHideRequested = MutableLiveData<Event<Unit>>()
    val keyboardHideRequested: LiveData<Event<Unit>>
        get() = _keyboardHideRequested



    fun requestFragment(request: IFragmentRequest, additionalExtras: IFragmentAdditionalExtras?) {
        _fragmentRequested.value = Event(Pair(request, additionalExtras))
    }


    fun requestKeyboardHide() {
        _keyboardHideRequested.value = Event(Unit)
    }


}