const moment = require('moment');
const _ = require('lodash');
const Lease = require('./model/Lease');

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
        const lease = this.leases.find(lease => lease.device.id === device.id);

        return {
            ...device,
            lease: !!lease ? lease : null
        };
    }

    allLeases() {
        return this.leases;
    }

    leaseAny() {
        const device = this.adbMonitor.preparedDevices
            .find(device => _.every(this.leases, lease => lease.device.id !== device.id));

        if (device) {
            const lease = new Lease(device, moment().add(10, 'minutes'));
            this.leases.push(lease);
            return lease;
        }
        throw new NoDevicesAvailable();
    }

    release(leaseId) {
        const removed = _.remove(this.leases, lease => lease.id === leaseId);
        if (_.isEmpty(removed)) {
            throw new LeaseNotFound(leaseId);
        }
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

class LeaseNotFound extends DeviceFarmError {
    constructor(leaseId) {
        super(`Lease not found with id:${leaseId}.`, 'LeaseNotFound');
    }
}

module.exports = {
    DeviceFarmService,
    NoDevicesAvailable,
    LeaseNotFound
};
