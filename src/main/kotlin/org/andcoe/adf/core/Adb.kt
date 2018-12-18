package org.andcoe.adf.core

class Adb(private val commandRunner: CommandRunner) {

    companion object {
        const val LOCAL_IP = "127.0.0.1"
    }

    fun killServer() {
        commandRunner.exec("adb kill-server")
    }

    fun startServer() {
        commandRunner.exec("adb start-server")
    }

    fun waitForDevice(deviceId: String) {
        commandRunner.exec("""adb -s $deviceId wait-for-device""")
    }

    fun devices(): List<String> {
        var output = commandRunner.exec("adb devices")
        output = output.replace("List of devices attached\n", "")
        return output.replace("device", "")
            .split("\n")
            .map { it.trim() }
            .filterNot { it.isEmpty() }
            .filterNot { it.startsWith(LOCAL_IP) }
    }

    fun deviceModelFor(deviceId: String): String {
        val output = commandRunner.exec("""adb -s $deviceId shell getprop ro.product.model""")
        return output.trim()
    }


    fun deviceManufacturerFor(deviceId: String): String {
        val output = commandRunner.exec("""adb -s $deviceId shell getprop ro.product.manufacturer""")
        return output.trim()
    }

    //
//    fun tcpIpFor(deviceId, timeout)
//    {
//        return this.exec(`adb -s ${deviceId} tcpip 5555`, { sleep: timeout })
//            .then(() => this.waitForDevice(deviceId))
//    }
//
    fun forwardFor(deviceId: String, localPort: Int, devicePort: Int) {
        commandRunner.exec("adb -s $deviceId forward tcp:$localPort tcp:${devicePort}")
    }

    fun connect(deviceId: String, port: Int) {
        commandRunner.exec("adb -s $deviceId connect $LOCAL_IP:$port")
    }

}