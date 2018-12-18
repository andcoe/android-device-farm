const DeviceFarmException = require('./device-farm.exception');

class NoDevicesAvailableException extends DeviceFarmException {
    constructor() {
        super('No devices available.', 'NoDevicesAvailableException');
    }
}

module.exports = NoDevicesAvailableException;