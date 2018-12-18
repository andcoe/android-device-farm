const DeviceDao = require('../../src/persistence/device.dao');
const Device = require('../../src/model/device');

describe('DeviceDao', () => {

    it('returns empty devices', () => {
        const deviceDao = new DeviceDao();
        expect(deviceDao.devices()).toEqual([]);
    });
    it('creates and returns devices', () => {
        const deviceDao = new DeviceDao();
        deviceDao.create(new Device('123'));
        deviceDao.create(new Device('456'));
        expect(deviceDao.devices()).toEqual([new Device('123'), new Device('456')]);
    });

});