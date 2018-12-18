const DeviceDao = require('../../src/persistence/device.dao');
const Device = require('../../src/model/device');

describe('DeviceDao', () => {
    describe('devices()', () => {
        it('returns empty devices', () => {
            const deviceDao = new DeviceDao();
            expect(deviceDao.devices()).toEqual([]);
        });
        it('returns devices', () => {
            const deviceDao = new DeviceDao();
            deviceDao.create(new Device('123'));
            expect(deviceDao.devices()).toEqual([new Device('123')]);
        });
    })
});