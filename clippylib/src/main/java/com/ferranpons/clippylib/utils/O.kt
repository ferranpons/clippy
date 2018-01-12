package com.ferranpons.clippylib.utils

import java.lang.RuntimeException

class O<E> {

    internal var data: E? = null
    val isSuccess: Boolean
    internal var error: String = ""

    constructor(data: E) {
        this.data = data
        this.isSuccess = true
    }

    constructor(error: String) {
        this.isSuccess = false
        this.error = error
    }

    fun getError(): String {
        if (isSuccess) {
            throw RuntimeException("Success: " + isSuccess)
        }
        return error
    }

    fun getData(): E? {
        if (!isSuccess) {
            throw RuntimeException("Success: " + isSuccess)
        }
        return data
    }


}
