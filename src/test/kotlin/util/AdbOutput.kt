package util

enum class AdbOutput(val output: String) {

    ADB_DEVICES(
        """
List of devices attached
127.0.0.1:7778	device
127.0.0.1:7777	device
XM043220	device
3204486bc15611b5	device


"""
    ),

    ADB_CONNECT_SUCCESS(
        """
            connected to 127.0.0.1:7777

"""
    ),

    ADB_START_SERVER(
        """
* daemon not running; starting now at tcp:5037
* daemon started successfully
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