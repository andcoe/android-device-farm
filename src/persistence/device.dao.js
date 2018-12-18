class DeviceDao {

    constructor() {
        this._devices = [];
    }

    devices() {
        return this._devices;
    }

    refreshDevices(devices) {
        this._devices = devices //TODO: remove the mutation
    }

    create(device) {
        this._devices.push(device);
        return device;
    }
}

module.exports = DeviceDao;