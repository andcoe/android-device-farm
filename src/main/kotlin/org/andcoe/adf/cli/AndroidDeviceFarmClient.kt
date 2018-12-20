package org.andcoe.adf.cli


import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.andcoe.adf.devices.Device
import java.io.PrintStream
import java.net.ConnectException

class MyArgs(parser: ArgParser) {


    companion object {
        val  subCommands = listOf("test", "devices", "leases")
    }

    val subCommand by parser.positional(
        "SUB_COMMAND",
        help = "subCommand"
    )
        .addValidator {
            if (!subCommands.contains(value))
                throw InvalidArgumentException(
                    "sub command must be one of ${subCommands}"
                )
        }

//
//    val upper by parser.flagging(
//        "-u", "--upper",
//        help = "enable upper case[p m/'ode"
//    )
//
//    val name by parser.storing(
//        "-n", "--name",
//        help = "name of the user"
//    )
//
//    val count by parser.storing(
//        "-c", "--count",
//        help = "number of widgets"
//    ) { toInt() }
//        .default(1)




}


/**
 *
 * farm devices
 * farm leases
 * farm test
 * farm test --device
 *
 */

fun main(args: Array<String>) = main(args, System.out)
fun main(args: Array<String>, out : PrintStream) {
    ArgParser(args).parseInto(::MyArgs).run {
        when(subCommand){
            "devices" -> getDevices(out)
        }
    }

}

fun getDevices(out : PrintStream) {
    val client = HttpClient() {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    runBlocking {
        var notConnected = true
        while(notConnected) {
            delay(500)
            try {
                val result = client.get<List<Device>>("http://0.0.0.0:8000/devices")
                out.println(result[0].deviceId.id)
                notConnected = false
            } catch (e: ConnectException) {
                println("NOT READY")
            }
        }
    }

}
