package com.mishenka.notbasic.managers.navigation

import com.mishenka.notbasic.interfaces.IFragmentRequest
import java.util.*

class ChildrenStack : Stack<IFragmentRequest>() {

    override fun pop(): IFragmentRequest? {
        return if (size == 0) {
            null
        } else {
            super.pop()
        }
    }

    override fun peek(): IFragmentRequest? {
        return if (size == 0) {
            null
        } else {
            super.peek()
        }
    }

}