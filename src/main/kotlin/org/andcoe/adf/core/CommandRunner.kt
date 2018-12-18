package org.andcoe.adf.core

import java.io.File
import java.util.concurrent.TimeUnit

interface CommandRunner {
    fun exec(command: String,
             workingDir: File = File("."),
             timeoutAmount: Long = 60,
             timeoutUnit: TimeUnit = TimeUnit.SECONDS): String
}

class AdbCommandRunner: CommandRunner {

    override fun exec(command: String,
                      workingDir: File,
                      timeoutAmount: Long,
                      timeoutUnit: TimeUnit): String {

        val process = ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        if (!process.waitFor(timeoutAmount, timeoutUnit)) {
            process.destroy()
            throw RuntimeException("execution timed out: $this")
        }

        if (process.exitValue() != 0) {
            throw RuntimeException("execution failed with code ${process.exitValue()}: $this")
        }

        return process.inputStream.bufferedReader().readText()
    }
}