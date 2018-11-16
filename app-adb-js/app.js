
var adb = require('adbkit')
var client = adb.createClient()

client.listDevices()
    .then(setupDevices)
    .catch((err) => console.error('Something went wrong3:', err.stack));


function setupDevices(devices) {
    console.log(devices);
    devices.map(setupDevice)
}

var numberOfAddedDevices = 0;

function setupDevice(device) {
    //console.log(device.id);
    client.tcpip(device.id)
        .then((deviceLocalPort) => {
            console.log("setup " + device.id + " on " + deviceLocalPort)
            // Switching to TCP mode causes ADB to lose the device for a
            // moment, so let's just wait till we get it back.

            console.log("tcpip activated successfully")
            var thisMachineLocalPort = deviceLocalPort + 1000 + numberOfAddedDevices++;
            console.log('about to setup forwarding on '+ device.id + ":"+deviceLocalPort + " to this machines local port:"+thisMachineLocalPort)
            client.forward(device.id, 'tcp:' +thisMachineLocalPort, 'tcp:' + deviceLocalPort) //ie tcp:6555
                .then(() => {
                    console.log('Setup forwarding on '+ device.id + ":"+deviceLocalPort + " to this machines local port:"+thisMachineLocalPort)
                })

        })
        .then(port => {
            //client.waitForDevice(device.id).return(port)
        })
        // .catch(e => {
        //     console.error("Error setting up tcpip on device: " + device.id, e);
        // })


}
