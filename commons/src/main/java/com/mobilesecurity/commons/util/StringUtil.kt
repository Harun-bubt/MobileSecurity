package com.mobilesecurity.commons.util

fun String.removeAllWhiteSpaces(): String {
    return this.replace("\\s".toRegex(), "")
}