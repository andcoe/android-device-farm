const DeviceFarmException = require('./device-farm.exception');

class LeaseNotFoundException extends DeviceFarmException {
    constructor(leaseId) {
        super(`Lease not found with id:${leaseId}.`, 'LeaseNotFoundException');
    }
}

module.exports = LeaseNotFoundException;