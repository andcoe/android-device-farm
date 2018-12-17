const Lease = require('../model/lease');
const moment = require('moment');
const _ = require('lodash');

class LeaseDao {
    constructor() {
        this.leases = [];
    }

    allLeases() {
        return this.leases;
    }

    create(device) {
        const lease = new Lease(device, moment().add(10, 'minutes'));
        this.leases.push(lease);
        return lease;
    }

    findByDeviceId(deviceId) {
        return this.leases.find(lease => lease.device.id === deviceId);
    }

    delete(leaseId) {
        return _.remove(this.leases, lease => lease.id === leaseId);
    }
}

module.exports = LeaseDao;