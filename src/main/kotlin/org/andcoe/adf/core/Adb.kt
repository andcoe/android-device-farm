package org.andcoe.adf.core

import sun.audio.AudioDevice.device

class Adb(private val commandRunner: CommandRunner) {

    companion object {
        const val LOCAL_IP = "127.0.0.1"
    }

//    fun killServer() {
//        return this.exec('adb kill-server', { ignoreStderr: true })
//    }
//
//    fun startServer() {
//        return this.exec('adb start-server', { ignoreStderr: true })
//    }

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

    //    fun deviceModelFor(deviceId)
//    {
//        return this.exec(`adb -s ${deviceId} shell getprop ro.product.model`)
//            .then(model => model . trim ())
//    }
//
//    fun deviceManufacturerFor(deviceId)
//    {
//        return this.exec(`adb -s ${deviceId} shell getprop ro.product.manufacturer`)
//            .then(manufacturer => manufacturer . trim ())
//    }
//
//    fun tcpIpFor(deviceId, timeout)
//    {
//        return this.exec(`adb -s ${deviceId} tcpip 5555`, { sleep: timeout })
//            .then(() => this.waitForDevice(deviceId))
//    }
//
    fun forwardFor(deviceId : String, localPort : Int, devicePort: Int)
    {
        commandRunner.exec("adb -s ${deviceId} forward tcp:${localPort} tcp:${devicePort}")
    }
//
//    fun forwardList(deviceId)
//    {
//        return this.exec(`adb -s ${deviceId} forward --list`)
//            .then(() => this.waitForDevice(deviceId))
//    }
//
    fun connect(deviceId: String, port: Int) {
        val output = commandRunner.exec("adb -s ${deviceId} connect ${LOCAL_IP}:${port}")
        if (!output.startsWith("connected to")) throw Error("unable to connect to: ${LOCAL_IP}:${port}");
    }

}