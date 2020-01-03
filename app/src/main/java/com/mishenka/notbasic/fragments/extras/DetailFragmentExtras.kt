package com.mishenka.notbasic.fragments.extras

import com.mishenka.notbasic.interfaces.IFragmentExtras

abstract class DetailFragmentExtras : IFragmentExtras {

    abstract val url: String

    override var fragmentId: Long = -1

}