const _ = require('lodash');
const Adb = require('./adb.js');

const WATCH_RETRY = 10000;

class AdbWatchDog {

    constructor() {
        this.preparedDevices = [];
    }

    start() {
        Adb.killServer()
            .then(() => Adb.startServer())
            .then(() => this.watchForNewDevices());
    }

    watchForNewDevices() {
        console.log('=========================================================================');
        console.log('============================ watchForNewDevices =========================');
        console.log('=========================================================================');
        Adb.devices()
            .then(devices => {
                const newDevices = _.differenceBy(devices, this.preparedDevices, !_.isEqual);
                console.log("Devices connected", newDevices);

                const removedDevices = _.differenceBy(this.preparedDevices, devices, _.isEqual);
                console.log("Devices removed", removedDevices);

                this.preparedDevices = _.differenceBy(this.preparedDevices, removedDevices, _.isEqual);
                console.log("Prepared devices", this.preparedDevices);

                return Promise.all(newDevices.map(device =>
                    //only add to prepared if setup is ok!
                    Adb.setupDevice(device.id, 250)
                        .then(() => this.preparedDevices.push(device))))
            })
            .catch(error => console.error('AdbWatchDog error:', error))
            .then(() => {
                console.log('AdbWatchDog sucess: scheduling another scan in ' + WATCH_RETRY + 'ms');
                // setTimeout(() => this.watchForNewDevices(), WATCH_RETRY)
            });
    }

}

module.exports = AdbWatchDog;