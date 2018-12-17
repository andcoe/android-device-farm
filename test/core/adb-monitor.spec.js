const AdbMonitor = require('../../src/core/adb-monitor');
const Device = require('../../src/model/device');
const moment = require('moment');

describe('AdbMonitor', () => {
    describe('borrow device', () => {
        it('does nothing if no devices', () => {
            const adbMonitor = new AdbMonitor();
            expect(adbMonitor.lease()).toEqual({});
        });
        it('leases it for some time', () => {
            const adbMonitor = new AdbMonitor();

            const device1 = new Device('123');
            device1.inUse = true;
            adbMonitor.preparedDevices.push(device1);

            const device2 = new Device('456');
            device2.inUse = false;
            adbMonitor.preparedDevices.push(device2);

            const startTime = moment();
            const leaseResponse = adbMonitor.lease();

            expect(leaseResponse.id).toEqual('456');
            expect(leaseResponse.inUse).toBe(true);
            expect(leaseResponse.inUseUntil).toBeDefined();

            const end = moment(leaseResponse.inUseUntil);

            const seconds = moment.duration(end.diff(startTime)).asSeconds();
            expect(seconds).toBeGreaterThan(1);
        });
    });
});