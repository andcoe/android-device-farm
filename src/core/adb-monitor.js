const _ = require('lodash');
const Adb = require('./adb.js');

const WATCH_RETRY = 10000;
const DEVICE_PORT = 5555;
let localPortCounter = 7777;

class AdbMonitor {

    constructor(deviceDao) {
        this.deviceDao = deviceDao;
    }

    start() {
        Adb.killServer()
            .then(() => Adb.startServer())
            .then(() => this.scanDevices());
    }

    scanDevices() {
        console.log('=========================================================================');
        console.log('============================== scanDevices ==============================');
        console.log('=========================================================================');
        Adb.devices()
            .then(devices => {
                console.log('AdbMonitor => all connected devices:');
                this.logDevices(devices);

                const preparedDevices = this.deviceDao.devices();

                const newDevices = _.differenceBy(devices, preparedDevices, device => device.id);
                console.log("AdbMonitor => connected now:");
                this.logDevices(newDevices);

                const removedDevices = _.differenceBy(preparedDevices, devices, device => device.id);
                console.log("AdbMonitor => removed now:");
                this.logDevices(removedDevices);

                const newPreparedDevices = _.differenceBy(preparedDevices, removedDevices, device => device.id);
                console.log("AdbMonitor => preparedDevices: ");
                this.logDevices(newPreparedDevices);

                this.deviceDao.refreshDevices(newPreparedDevices);

                return Promise.all(newDevices.map(device =>
                    //only add to prepared if setup is ok!
                    this.setupDevice(device, 250)
                        .then(() => this.deviceDao.create(device))))
            })
            .catch(error => console.error('AdbMonitor => error:', error))
            .then(() => {
                console.log('AdbMonitor => scheduling another scan in ' + WATCH_RETRY + 'ms');
                setTimeout(() => this.scanDevices(), WATCH_RETRY)
            });
    }

    /** @returns {Promise} */
    setupDevice(device, timeout) {
        console.log('AdbMonitor => setupDevice:', device.id);
        let localPort = localPortCounter++;
        return Adb.tcpIpFor(device.id, timeout)
            .then(() => Adb.forwardFor(device.id, localPort, DEVICE_PORT))
            // .then(() => Adb.forwardList(device.id))
            .then(() => Adb.connect(device.id, localPort))
            .then(() => Adb.deviceModelFor(device.id)
                .then(model => Adb.deviceManufacturerFor(device.id)
                    .then(manufacturer => {
                        device.setManufacturer(manufacturer);
                        device.setModel(model);
                        device.setPort(localPort);
                    })));
    }

    logDevices(devices) {
        if (!_.isEmpty(devices)) {
            console.log("[");
            devices.forEach(d => console.log('  ' + JSON.stringify(d)));
            console.log(']');
        } else {
            console.log("[]");
        }
    }

}

module.exports = AdbMonitor;