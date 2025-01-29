package com.nurdev.notener.utils

fun isDigitsOnly(str: String): Boolean {
    return str.all { it.isDigit() }
}

fun removeLines(str: String): String {
    return str.lines().filter { it.isNotEmpty() }.joinToString("\n")
}