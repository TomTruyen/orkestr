package com.tomtruyen.automation.core.utils

object SelectionCodec {
    fun encode(values: Collection<String>): String = values
        .filter(String::isNotBlank)
        .distinct()
        .sorted()
        .joinToString(",")

    fun decode(raw: String): Set<String> = raw.split(',')
        .map(String::trim)
        .filter(String::isNotBlank)
        .toSet()
}
