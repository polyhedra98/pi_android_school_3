package com.mishenka.notbasic.managers.navigation

import java.util.*

class ChildrenStack : Stack<RequestItem>() {

    override fun pop(): RequestItem? {
        return if (size == 0) {
            null
        } else {
            super.pop()
        }
    }

    override fun peek(): RequestItem? {
        return if (size == 0) {
            null
        } else {
            super.peek()
        }
    }

}