
class DeviceFarmService {

    constructor(adbMonitor) {
        this.adbMonitor = adbMonitor;
    }

    start() {
        this.adbMonitor.start();
    }

    devices() {
        return this.adbMonitor.preparedDevices;
    }

    leaseAny() {
        return this.adbMonitor.lease();
    }

    release() {

    }
}

module.exports = DeviceFarmService;