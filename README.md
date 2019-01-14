# android-device-farm

### Objective:

Android device farm is a tool that monitors and manages android devices that are connected to a machine (e.g raspberry pi).

### How:

Clients can acquire a device (lease) from the device farm using the rest api (shown below) and perform various tasks with it, e.g. running Android Espresso tests.

### Architecture:

<pre>

                                                                                                       +-+
                                                                                               +------^+ | device1
                                                      +---------------+     +-----------+      |       +-+   .
                                                      |               |     |           +------+             .
                                                      |  AdbMonitor   +----->   adb     |                    .
                                                      |               |     |           +------+       +-+   .
                                                      +-------+-------+     +-----------+      +------^+ | deviceN
                                                              |                                        +-+
                                                              |
+---------------------------+   +-----------------+   +-------v---------+   +-----------------+
|                           |   |                 |   |                 |   |                 |      XXXXXXXXXXXXX
|  GET  /deVices            |   |                 |   |                 |   |                 |     X             X
|  GET  /devices/{deviceId  +---> devicesResource +---> devicesService  +--->   devicesDao    +--->X devicesStore  X
|                           |   |                 |   |                 |   |                 |    X               X
|                           |   |                 |   |                 |   |                 |     X             X
+---------------------------+   +-----------------+   +-----------------+   +-----------------+      XXXXXXXXXXXXX

+---------------------------+   +-----------------+   +-----------------+   +-----------------+
|                           |   |                 |   |                 |   |                 |      XXXXXXXXXXXXX
| GET    /leases            |   |                 |   |                 |   |                 |     X             X
| POST   /leases            +---> leasesResource  +--->  leasesService  +--->    leasesDao    +--->X leasesStore   X
| POST   /leases/{deviceId} |   |                 |   |                 |   |                 |    X               X
| DELETE /leases/{leaseId}  |   |                 |   |                 |   |                 |     X             X
+---------------------------+   +-----------------+   +-----------------+   +-----------------+      XXXXXXXXXXXXX
</pre>

# Other useful stuff

## setup pi:
https://www.raspberrypi.org/documentation/configuration/
## setup wifi

### setup ssh
https://www.raspberrypi.org/documentation/configuration/raspi-config.md
ssh pi@<ip>

### install adb:
sudo apt-get install -y android-tools-adb android-tools-fastboot

ssh -f pi@<ip> -L 6555:localhost:6555 -N
