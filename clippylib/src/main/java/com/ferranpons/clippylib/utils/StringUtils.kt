package com.ferranpons.clippylib.utils

import java.util.Locale

object StringUtils {

    fun isValid(string: String?): Boolean {
        return string != null && string.isNotEmpty()
    }

    fun capitalize(line: String): String {
        return (Character.toUpperCase(line[0]) + line.substring(1).toLowerCase(Locale.US))
    }
}
