package org.andcoe.adf.core

class Adb(private val commandRunner: CommandRunner) {

//    fun killServer()
//    {
//        return this.exec('adb kill-server', { ignoreStderr: true })
//    }
//
//    fun startServer()
//    {
//        return this.exec('adb start-server', { ignoreStderr: true })
//    }
//
//    fun waitForDevice(deviceId)
//    {
//        return this.exec(`adb -s ${deviceId} wait-for-device`, { timeout: 15000 })
//    }

    fun devices(): List<String> {
        var output = commandRunner.exec("adb devices")
        output = output.replace("List of devices attached\n", "")
        return output.replace("\\sdevice/g", "\n").split("\n")

//        .then(result => result . replace ( / \ sdevice / g, '\n').split('\n'))
//        .then(result => result . filter (id =>!!id))
//        .then(result => result . filter (id =>!id.startsWith(LOCAL_IP)))
//        .then(result => Promise . all (result.map(id => new Device(id))));
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
//    fun forwardFor(deviceId, localPort, devicePort)
//    {
//        return this.exec(`adb -s ${deviceId} forward tcp:${localPort} tcp:${devicePort}`)
//            .then(() => this.waitForDevice(deviceId))
//    }
//
//    fun forwardList(deviceId)
//    {
//        return this.exec(`adb -s ${deviceId} forward --list`)
//            .then(() => this.waitForDevice(deviceId))
//    }
//
//    fun connect(deviceId, port)
//    {
//        return this.exec(`adb -s ${deviceId} connect ${LOCAL_IP}:${port}`)
//            .then(result => {
//                if (!result.startsWith('connected to')) throw Error(`unable to connect to: ${LOCAL_IP}:${port}`);
//            })
//        .then(() => this.waitForDevice(deviceId))
//    }

}