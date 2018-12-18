const AdbMonitor = require('../../src/core/adb-monitor');
const Adb = require('../../src/core/adb');
const DeviceService = require('../../src/service/device.service');

describe('AdbMonitor', () => {
    describe('constructor', () => {
        it('throws if no device service', () => {
            expect(() => { new AdbMonitor(); }).toThrow();
        });
        it('throws if wrong device service', () => {
            expect(() => { new AdbMonitor({}); }).toThrow();
        });
        it('throws if no adb', () => {
            expect(() => { new AdbMonitor(new DeviceService()); }).toThrow();
        });
        it('throws if wrong adb', () => {
            expect(() => { new AdbMonitor(new DeviceService(), {}); }).toThrow();
        });
        it('creates it', () => {
            const deviceService = new DeviceService();
            const adb = new Adb();
            const adbMonitor = new AdbMonitor(deviceService, adb);
            expect(adbMonitor.deviceService).toBe(deviceService);
            expect(adbMonitor.adb).toBe(adb);
        });
    });
    // describe('start()', () => {
    //     const killAdbServerPromise = new Promise((resolve, reject) => resolve('foo'));
    //     const startAdbServerPromise = new Promise((resolve, reject) => resolve('foo'));
    //
    //     it('kills adb', () => {
    //         const deviceService = new DeviceService();
    //         const adb = new Adb();
    //         const adbMonitor = new AdbMonitor(deviceService, adb);
    //         spyOn(adb, 'killServer').and.returnValue(killAdbServerPromise);
    //         spyOn(adb, 'startServer').and.returnValue(startAdbServerPromise);
    //         adbMonitor.start();
    //         expect(adb.killServer).toHaveBeenCalled();
    //     });
    // });
});