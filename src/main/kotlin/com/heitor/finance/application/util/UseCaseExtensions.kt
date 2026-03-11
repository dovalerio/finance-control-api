package com.heitor.finance.application.util

fun <T> T?.orThrow(exception: () -> RuntimeException): T =
    this ?: throw exception()
