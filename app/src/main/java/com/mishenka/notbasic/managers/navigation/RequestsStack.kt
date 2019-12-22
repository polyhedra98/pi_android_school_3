package com.mishenka.notbasic.managers.navigation

import java.util.*

class RequestsStack : Stack<RequestsStackItem>() {

    val totalCount: Int
        get() {
            var totalCount = 0
            for (stackItem in elements()) {
                totalCount++
                stackItem.children?.let { safeChildren ->
                    for (child in safeChildren) {
                        totalCount++
                    }
                }
            }
            return totalCount
        }

    val primaryCount: Int
        get() = size


    override fun pop(): RequestsStackItem? {
        return if (size == 0) {
            null
        } else {
            super.pop()
        }
    }


    override fun peek(): RequestsStackItem? {
        return if (size == 0) {
            null
        } else {
            super.peek()
        }
    }

}