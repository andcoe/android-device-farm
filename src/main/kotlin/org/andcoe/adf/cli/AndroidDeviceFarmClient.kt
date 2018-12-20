package org.andcoe.adf.cli


import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class MyArgs(parser: ArgParser) {
    val upper by parser.flagging(
            "-u", "--upper",
            help = "enable upper case[p m/'ode")

    val name by parser.storing(
            "-n", "--name",
            help = "name of the user")

    val count by parser.storing(
            "-c", "--count",
            help = "number of widgets") { toInt() }
            .default(1)

    val device1 by parser.positional(
            "DEVICE_ONE",
            help = "source filename")
            .default<String?>(null)

    val device2 by parser.positional(
            "DEVICE_TWO",
            help = "destination filename")
}


fun main(args: Array<String>) {
    ArgParser(args).parseInto(::MyArgs).run {

        device1?.let{
            println("Optional positional: ${device1}!")
        }
        println("Hello, ${name}!")
        println("I'm going to run ${count} tests on ${device1} and ${device2}.")
        // TODO: move widgets
    }

}