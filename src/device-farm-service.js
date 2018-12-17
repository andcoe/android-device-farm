const moment = require("moment");
const _ = require('lodash');

class DeviceFarmService {

    constructor(adbMonitor) {
        this.adbMonitor = adbMonitor;
        this.leases = [];
    }

    start() {
        this.adbMonitor.start();
    }

    devices() {
        return this.adbMonitor.preparedDevices
            .map(device => this.device(device.id));
    }

    device(id) {
        const device = this.adbMonitor.preparedDevices.find(device => device.id === id);
        //never more than 1 matching lease
        //no stale leases
        const lease = this.leases.find(lease => lease.deviceId === device.id);

        return {
            ...device,
            inUse: !!lease,
            inUseUntil: !!lease ? lease.inUseUntil : null
        };
    }

    allLeases() {
        return this.leases;
    }

    leaseAny() {
        const device = this.adbMonitor.preparedDevices
            .find(device => _.every(this.leases, lease => lease.deviceId !== device.id));

        if (device) {
            this.leases.push(new Lease(device.id, moment().add(10, 'minutes')));
            return this.device(device.id);
        }
        throw new NoDevicesAvailable()
    }
}


class DeviceFarmError extends Error {
    constructor(message, code) {
        super(message);
        this.code = code;
    }
}

class NoDevicesAvailable extends DeviceFarmError {
    constructor() {
        super('No devices available.', 'NoDevicesAvailable');
    }
}

class Lease {

    constructor(deviceId, inUseUntil) {
        this.deviceId = deviceId;
        this.inUseUntil = inUseUntil;
    }
}

module.exports = {
    DeviceFarmService,
    NoDevicesAvailable
};
