const _ = require('lodash');
const NoDevicesAvailableException = require('./model/exception/no-devices-available.exception');
const LeaseNotFoundException = require('./model/exception/lease-not-found.exception');

class DeviceFarmService {

    constructor(adbMonitor, leaseDao) {
        this.adbMonitor = adbMonitor;
        this.leaseDao = leaseDao;
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
        const lease = this.leaseDao.findByDeviceId(device.id);

        return {
            ...device,
            lease: !!lease ? lease : null
        };
    }

    allLeases() {
        return this.leaseDao.allLeases();
    }

    leaseAny() {
        const leases = this.leaseDao.allLeases();

        const device = this.adbMonitor.preparedDevices
            .find(device => _.every(leases, lease => lease.device.id !== device.id));

        if (device) {
            return this.leaseDao.create(device);
        }
        throw new NoDevicesAvailableException();
    }

    release(leaseId) {
        const removed = this.leaseDao.delete(leaseId);
        if (_.isEmpty(removed)) {
            throw new LeaseNotFoundException(leaseId);
        }
    }
}

module.exports = DeviceFarmService;
