const adb = require('adbkit');
const client = adb.createClient();

client.listDevices()
    .then(setupDevices)
    .catch((err) => console.error('Something went wrong3:', err.stack));


function setupDevices(devices) {
    console.log(devices);
    devices.map(setupDevice)
}

let numberOfAddedDevices = 0;

function setupDevice(device) {
    //console.log(device.id);
    client.tcpip(device.id)
        .then((deviceLocalPort) => {
            console.log("setup " + device.id + " on " + deviceLocalPort)
            // Switching to TCP mode causes ADB to lose the device for a
            // moment, so let's just wait till we get it back.

            console.log("tcpip activated successfully")
            var thisMachineLocalPort = deviceLocalPort + 1000 + numberOfAddedDevices++;
            console.log('about to setup forwarding on ' + device.id + ":" + deviceLocalPort + " to this machines local port:" + thisMachineLocalPort)
            client.forward(device.id, 'tcp:' + thisMachineLocalPort, 'tcp:' + deviceLocalPort) //ie tcp:6555
                .then(() => {
                    console.log('Setup forwarding on ' + device.id + ":" + deviceLocalPort + " to this machines local port:" + thisMachineLocalPort)
                })

        })
        .then(port => {
            //client.waitForDevice(device.id).return(port)
        });
    // .catch(e => {
    //     console.error("Error setting up tcpip on device: " + device.id, e);
    // })
}



// listForwards();

// tcpIpFor("HT73R0205355")
//      .then(port => console.log(port));

// waitForDevice("HT73R0205355")
//      .then(id => console.log(id));

listDevices()
    .then(devices => console.log(devices));

// listDevices()
//     .then(devices => devices.map(device => client.getFeatures(device.id)
//         .then(s => console.log(s))
//     ));

/**
 * Gets the list of currently connected devices and emulators.
 * command: adb devices
 * @returns {Promise<Array<Object>>}
 */
function listDevices() {
    return client
        .listDevices()
        .catch(error => console.error(error));
}

/**
 * Lists forwarded connections on the device.
 * command: adb forward --list
 * @returns {Promise}
 */
function listForwards() {
    return client
        .listForwards()
        .then(forwards => {
            console.log("forwards ====> ", JSON.stringify(forwards));
            return forwards.map(f => {
                console.log("forward ====> ", f);
                return f;
            })
        })
        .catch(error => console.error(error));
}

/**
 * Puts the device's ADB daemon into tcp mode,
 * allowing you to use adb connect or client.connect() to connect to it.
 * Note that the device will still be visible to ADB as a
 * regular USB-connected device until you unplug it.
 * command: adb tcpip <port>
 * @returns {Promise}
 */
function tcpIpFor(deviceId) {
    return client
        .tcpip(deviceId)
        .then(port => {
            console.log("tcpIp ====> setup for " + deviceId + " on port:", port);
            return port;
        })
        .catch(error => console.error(error));
}

/**
 * Waits until ADB can see the device.
 * Note that you must know the serial in advance.
 * Other than that, works like adb -s serial wait-for-device.
 * If you're planning on reacting to random devices being
 * plugged in and out, consider using client.trackDevices() instead.
 * command: adb -s <serial> wait-for-device
 * @returns {Promise}
 */
function waitForDevice(deviceId) {
    return client
        .waitForDevice(deviceId)
        .then(id => {
            console.log("waitForDevice ====> " + id + " is now online ", id);
            return id;
        })
        .catch(error => console.error(error));
}
