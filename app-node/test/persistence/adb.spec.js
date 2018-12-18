const Adb = require('../../src/core/adb');

const MockChildProcess = require('../../src/mock/child-process')

class MockCommand {
    constructor(command, code, stdout, stderr) {
        this.command = command;
        this.code = code;
        this.stdout = stdout;
        this.stderr = stderr;
        this.nextCommand = null
    }
}


const adbDevices_OneDevice = new MockCommand("adb devices", 0,
`List of devices attached
FA79L1A04130    device

`, null);

const adbDevices_NoDevice = new MockCommand("adb devices", 0,
`List of devices attached

`,
    null);


describe('Adb', () => {

    it('adb devices -> is empty', (done) => {
        MockChildProcess.enqueue(adbDevices_NoDevice);

        const adb = new Adb(MockChildProcess);
        adb.devices()
            .then(devices => expect(devices.length).toEqual(0))
            .finally(done);
    });


    it('adb devices -> has one device', (done) => {
        MockChildProcess.enqueue(adbDevices_OneDevice);

        const adb = new Adb(MockChildProcess);
        adb.devices()
            .then(devices => expect(devices.length).toEqual(1))
            .finally(done);
    });


});