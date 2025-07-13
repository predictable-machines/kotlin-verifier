@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * Examples that mutate collections passed from the caller.
 */
object MutableCollectionExamples {

    fun addItem(list: MutableList<String>, item: String): List<String> {
        list.add(item) // Mutates external list
        return list
    }

    fun removeFirst(list: MutableList<String>): String? {
        return if (list.isNotEmpty()) list.removeAt(0) else null
    }
} 
