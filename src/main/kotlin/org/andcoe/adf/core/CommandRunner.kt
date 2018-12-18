package org.andcoe.adf.core

import java.util.concurrent.TimeUnit

class CommandRunner {

    fun exec(command: String, timeoutAmount: Long = 15, timeoutUnit: TimeUnit = TimeUnit.SECONDS): String {
        println("""> $command""")

        val process = ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
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

        val output = process.inputStream.bufferedReader().readText()
        println("""< $output""")
        return output
    }
}