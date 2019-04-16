package org.andcoe.adf.core

import org.andcoe.adf.devices.DeviceId
import java.util.concurrent.TimeUnit

class LocalCommandRunner : CommandRunner() {

    private val registeredCommands = mutableMapOf<String, String>()

    init {
        register("adb kill-server", AdbOutput.ADB_KILL_SERVER.output)
        register("adb start-server", AdbOutput.ADB_START_SERVER.output)

        registerAdbCommandsForDevice(
            deviceId = AdbOutput.DEVICE_PIXEL.deviceId,
            tcpIpPort = 7777,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_PIXEL,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_GOOGLE,
            adbAndroidVersionResponse = AdbOutput.ADB_ANDROID_VERSION_PIXEL,
            adbApiLevelResponse = AdbOutput.ADB_API_LEVEL_PIXEL
        )
        registerAdbCommandsForDevice(
            deviceId = AdbOutput.DEVICE_SAMSUNG.deviceId,
            tcpIpPort = 7778,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_S9,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_SAMSUNG,
            adbAndroidVersionResponse = AdbOutput.ADB_ANDROID_VERSION_S9,
            adbApiLevelResponse = AdbOutput.ADB_API_LEVEL_S9
        )

        register("adb devices", AdbOutput.ADB_DEVICES.output)
    }

    override fun exec(command: String, timeoutAmount: Long, timeoutUnit: TimeUnit): String {
        println("""LOCAL > $command""")

        val output = registeredCommands[command] ?: throw IllegalStateException("Unknown command: {$command}")

        println("""LOCAL < $output""")
        return output
    }

    private fun register(adbCommand: String, adbResult: String) {
        print("""registering> adbCommand: $adbCommand ->  adbResult: $adbResult""")
        registeredCommands[adbCommand] = adbResult
    }

    private fun registerAdbCommandsForDevice(
        deviceId: DeviceId,
        tcpIpPort: Int,
        adbModelResponse: AdbOutput,
        adbManufacturerResponse: AdbOutput,
        adbAndroidVersionResponse: AdbOutput,
        adbApiLevelResponse: AdbOutput
    ) {
        register("adb -s ${deviceId.id} tcpip 5555", AdbOutput.ADB_TCP_IP.output)
        register("adb -s ${deviceId.id} wait-for-device", AdbOutput.ADB_WAIT_FOR_DEVICE.output)
        register("adb -s ${deviceId.id} forward tcp:$tcpIpPort tcp:5555", AdbOutput.ADB_FORWARD_IP.output)
        register("adb -s ${deviceId.id} connect 127.0.0.1:$tcpIpPort", AdbOutput.ADB_CONNECT_SUCCESS.output)
        register("adb -s ${deviceId.id} shell getprop ro.product.model", adbModelResponse.output)
        register("adb -s ${deviceId.id} shell getprop ro.product.manufacturer", adbManufacturerResponse.output)
        register("adb -s ${deviceId.id} shell getprop ro.build.version.release", adbAndroidVersionResponse.output)
        register("adb -s ${deviceId.id} shell getprop ro.build.version.sdk", adbApiLevelResponse.output)
    }
}