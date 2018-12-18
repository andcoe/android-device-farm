package org.andcoe.adf

import org.andcoe.adf.core.Adb
import org.andcoe.adf.core.AdbCommandRunner

fun main(args: Array<String>) {
    println(Adb(AdbCommandRunner()).devices())
}

