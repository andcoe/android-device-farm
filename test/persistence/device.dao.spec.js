const DeviceDao = require('../../src/persistence/device.dao');

describe('DeviceDao', () => {
    it('returns empty devices', () => {
        const deviceDao = new DeviceDao();
        expect(deviceDao.allDevices()).toEqual([]);
    });
});