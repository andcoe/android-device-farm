class DeviceDao {

    constructor() {
        this.devices = [];
    }

    allDevices() {
        return this.devices;
    }

    create(device) {
        this.devices.push(device);
        return device;
    }
}

module.exports = DeviceDao;