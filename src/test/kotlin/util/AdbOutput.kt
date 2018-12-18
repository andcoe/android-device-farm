package util

enum class AdbOutput(val output: String) {
    ADB_KILL_SERVER(""),
    ADB_START_SERVER(
        """
* daemon not running; starting now at tcp:5037
* daemon started successfully
"""
    ),
    ADB_WAIT_FOR_DEVICE(""),
    ADB_DEVICES(
        """
List of devices attached
127.0.0.1:7778	device
127.0.0.1:7777	device
PIXEL	device
SAMSUNG	device


"""
    ),
    ADB_DEVICES_EMPTY(
        """
List of devices attached


"""
    ),
    ADB_TCP_IP(""),
    ADB_FORWARD_IP(""),
    ADB_CONNECT_SUCCESS(
        """
            connected to 127.0.0.1:7777

"""
    ),
    ADB_DEVICE_MODEL(
        """
    Aquaris X5 Plus
"""
    ),
    ADB_DEVICE_MANUFACTURER(
        """
    bq
"""
    )

}