const child_process = require('child_process');
const _ = require('lodash');
const Device = require('../model/device.js');

const LOCAL_IP = '127.0.0.1';

class Adb {

    static exec(command, options) {
        options = options || {
            timeout: options && options.timeout,
            ignoreStderr: options && options.ignoreStderr || false,
            sleep: options && options.sleep
        };
        console.log('>', command, options);

        return new Promise((resolve, reject) => {
            let timeoutId;
            if (options.timeout) {
                timeoutId = setTimeout(() => {
                    console.log('adb timeout after ' + options.timeout + 'ms killing pid:', cp.pid);
                    if (!cp) throw Error('child process is not present');
                    cp.kill();
                }, options.timeout);
            }

            const cp = child_process.exec(command, (error, stdout, stderr) => {
                if (options.timeout) clearTimeout(timeoutId);

                if (error) {
                    console.error('Non-empty error channel:\n');
                    console.error(error);
                    reject(error);
                    return;
                }

                if (!options.ignoreStderr && stderr) {
                    console.error('Non-empty stderr channel:\n');
                    console.error(stderr);
                    reject(stderr);
                    return;
                }

                console.log(stdout);
                if (options.sleep) setTimeout(() => resolve(stdout), options.sleep);
                else resolve(stdout);

            });
        });
    }

    /** @returns {Promise} */
    static killServer() {
        return this.exec('adb kill-server', {ignoreStderr: true})
    }

    /** @returns {Promise} */
    static startServer() {
        return this.exec('adb start-server', {ignoreStderr: true})
    }

    /** @returns {Promise} */
    static waitForDevice(deviceId) {
        return this.exec(`adb -s ${deviceId} wait-for-device`, {timeout: 15000})
    }

    /** @returns {Promise<Device[]>} */
    static devices() {
        return this.exec('adb devices')
            .then(result => result.replace('List of devices attached\n', ''))
            .then(result => result.replace(/\sdevice/g, '\n').split('\n'))
            .then(result => result.filter(id => !!id))
            .then(result => result.filter(id => !id.startsWith(LOCAL_IP)))
            .then(result => Promise.all(result.map(id => new Device(id))));
    }

    /** @returns {Promise<String>} */
    static deviceModelFor(deviceId) {
        return this.exec(`adb -s ${deviceId} shell getprop ro.product.model`)
            .then(model => model.trim())
    }

    /** @returns {Promise<String>} */
    static deviceManufacturerFor(deviceId) {
        return this.exec(`adb -s ${deviceId} shell getprop ro.product.manufacturer`)
            .then(manufacturer => manufacturer.trim())
    }

    /** @returns {Promise} */
    static tcpIpFor(deviceId, timeout) {
        return this.exec(`adb -s ${deviceId} tcpip 5555`, {sleep: timeout})
            .then(() => this.waitForDevice(deviceId))
    }

    /** @returns {Promise} */
    static forwardFor(deviceId, localPort, devicePort) {
        return this.exec(`adb -s ${deviceId} forward tcp:${localPort} tcp:${devicePort}`)
            .then(() => this.waitForDevice(deviceId))
    }

    /** @returns {Promise} */
    static forwardList(deviceId) {
        return this.exec(`adb -s ${deviceId} forward --list`)
            .then(() => this.waitForDevice(deviceId))
    }

    /** @returns {Promise} */
    static connect(deviceId, port) {
        return this.exec(`adb -s ${deviceId} connect ${LOCAL_IP}:${port}`)
            .then(result => {
                if (!result.startsWith('connected to')) throw Error(`unable to connect to: ${LOCAL_IP}:${port}`);
            })
            .then(() => this.waitForDevice(deviceId))
    }
}

module.exports = Adb;