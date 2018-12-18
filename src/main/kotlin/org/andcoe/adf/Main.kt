package org.andcoe.adf

import org.andcoe.adf.core.Adb
import org.andcoe.adf.core.CommandRunner

fun main(args: Array<String>) {
    println(Adb(CommandRunner()).devices())
}

