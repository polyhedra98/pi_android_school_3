package com.mishenka.notbasic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishenka.notbasic.interfaces.IFragmentRequest
import com.mishenka.notbasic.utils.Event
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val eventsModule = module {
    viewModel { EventVM() }
}

class EventVM: ViewModel() {

    private val _fragmentRequested = MutableLiveData<Event<IFragmentRequest>>()
    val fragmentRequested: LiveData<Event<IFragmentRequest>>
        get() = _fragmentRequested


    fun requestFragment(request: IFragmentRequest) {
        _fragmentRequested.value = Event(request)
    }

}