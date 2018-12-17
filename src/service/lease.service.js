const _ = require('lodash');
const NoDevicesAvailableException = require('../model/exception/no-devices-available.exception');
const LeaseNotFoundException = require('../model/exception/lease-not-found.exception');

class LeaseService {

    constructor(leaseDao, deviceDao) {
        this.leaseDao = leaseDao;
        this.deviceDao = deviceDao;
    }

    allLeases() {
        return this.leaseDao.allLeases();
    }

    leaseAny() {
        const leases = this.leaseDao.allLeases();

        const device = this.deviceDao.allDevices()
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

module.exports = LeaseService;