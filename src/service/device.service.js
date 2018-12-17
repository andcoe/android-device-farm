const _ = require('lodash');

class DeviceService {

    constructor(deviceDao, leaseDao) {
        this.deviceDao = deviceDao;
        this.leaseDao = leaseDao;
    }

    devices() {
        return this.deviceDao.allDevices()
            .map(device => this.device(device.id));
    }

    device(id) {
        const device = this.deviceDao.allDevices().find(device => device.id === id);
        //never more than 1 matching lease
        //no stale leases
        const lease = this.leaseDao.findByDeviceId(device.id);

        return {
            ...device,
            lease: !!lease ? lease : null
        };
    }


}

module.exports = DeviceService;
