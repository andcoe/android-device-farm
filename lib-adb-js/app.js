const adb = require('adbkit');
const client = adb.createClient();

// client.listDevices()
//     .then(setupDevices)
//     .catch((err) => console.error('Something went wrong3:', err.stack));
//
//
// function setupDevices(devices) {
//     console.log(devices);
//     devices.map(setupDevice)
// }
//
// let numberOfAddedDevices = 0;
//
// function setupDevice(device) {
//     //console.log(device.id);
//     client.tcpip(device.id)
//         .then((deviceLocalPort) => {
//             console.log("setup " + device.id + " on " + deviceLocalPort)
//             // Switching to TCP mode causes ADB to lose the device for a
//             // moment, so let's just wait till we get it back.
//
//             console.log("tcpip activated successfully")
//             var thisMachineLocalPort = deviceLocalPort + 1000 + numberOfAddedDevices++;
//             console.log('about to setup forwarding on ' + device.id + ":" + deviceLocalPort + " to this machines local port:" + thisMachineLocalPort)
//             client.forward(device.id, 'tcp:' + thisMachineLocalPort, 'tcp:' + deviceLocalPort) //ie tcp:6555
//                 .then(() => {
//                     console.log('Setup forwarding on ' + device.id + ":" + deviceLocalPort + " to this machines local port:" + thisMachineLocalPort)
//                 })
//
//         })
//         .then(port => {
//             //client.waitForDevice(device.id).return(port)
//         });
//     // .catch(e => {
//     //     console.error("Error setting up tcpip on device: " + device.id, e);
//     // })
// }


// const thisMachineLocalPort = 6555;
//
// listDevices()
//   .then(devices => {
//     console.log(devices)
//     devices.forEach(device => {
//       tcpIpFor(device.id)
//         .then(devicePort => forwardFor(device.id, devicePort))
//         .then(result => console.log(result));
//     });
//   });


// listForwards("0123456789ABCDEF");
// forwardFor("0123456789ABCDEF").then(result => console.log(result));
// tcpIpFor("HT73R0205355").then(port => console.log(port));
// waitForDevice("0123456789ABCDEF").then(id => console.log(id));
// listDevices().then(devices => console.log(devices));

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
  console.log('trying to setup tcpIpFor:', deviceId);
  return client
    .tcpip(deviceId)
    .then(port => {
      console.log("tcpIp ====> setup for " + deviceId + " on port:", port);
      return port;
    })
    .catch(error => console.error('tcpip command failed:', error));
}

/**
 * Forwards socket connections from the ADB server host (local) to the
 * device (remote). This is analogous to adb forward <local> <remote>.
 * It's important to note that if you are connected to a remote ADB server,
 * the forward will be created on that host.
 * command: adb -s deviceId forward tcp:machinePort tcp:devicePort
 * @returns {Promise<boolean>}
 */
function forwardFor(deviceId, devicePort) {
  console.log('trying to setup forwardFor:', deviceId);
  return client.forward(deviceId, 'tcp:' + thisMachineLocalPort, 'tcp:' + devicePort)
    .then(result => {
      console.log('Setup forwarding on ' + deviceId + ":" + devicePort + " to this machines local port:" + thisMachineLocalPort)
      return result;
    })
    .catch(error => console.error('forwardFor command failed', error));
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
