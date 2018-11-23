const child_process = require('child_process');
const _ = require('lodash');

const LOCAL_IP = '127.0.0.1';
let localPortCounter = 7777;
const devicePort = 5555;
let preparedDevices = [];

class Device {
  constructor(id, manufacturer, model) {
    this.id = id;
    this.manufacturer = manufacturer;
    this.model = model;
  }
}

function exec(command, options) {
  options = options || {
    timeout: options && options.timeout,
    ignoreStderr: options && options.ignoreStderr || false,
    sleep: options && options.sleep
  };
  console.log('>', command, options);

  return new Promise((resolve, reject) => {
    let timeoutId;
    if (options.timeout) {
      timeoutId = setTimeout(() => {
        console.log('adb timeout after ' + options.timeout + 'ms killing pid:', cp.pid);
        if (!cp) throw Error('child process is not present');
        cp.kill();
      }, options.timeout);
    }

    const cp = child_process.exec(command, (error, stdout, stderr) => {
      if (options.timeout) clearTimeout(timeoutId);

      if (error) {
        console.error('Non-empty error channel:\n');
        console.error(error);
        reject(error);
        return;
      }

      if (!options.ignoreStderr && stderr) {
        console.error('Non-empty stderr channel:\n');
        console.error(stderr);
        reject(stderr);
        return;
      }

      console.log(stdout);
      if (options.sleep) setTimeout(() => resolve(stdout), options.sleep);
      else resolve(stdout);

    });
  });
}

/** @returns {Promise<void>} */
function killServer() {
  return exec('adb kill-server', {
    ignoreStderr: true
  })
}

/** @returns {Promise<void>} */
function startServer() {
  return exec('adb start-server', {
    ignoreStderr: true
  })
}

/** @returns {Promise<void>} */
function waitForDevice(deviceId) {
  return exec(`adb -s ${deviceId} wait-for-device`, {
    timeout: 15000
  })
}

/** @returns {Promise<Device[]>} */
function devices() {
  return exec('adb devices')
    .then(result => result.replace('List of devices attached\n', ''))
    .then(result => result.replace(/\sdevice/g, '\n').split('\n'))
    .then(result => result.filter(id => !!id))
    .then(result => result.filter(id => !id.startsWith(LOCAL_IP)))
    .then(result => Promise.all(result.map(id => {
      return deviceModelFor(id)
        .then(model => deviceManufacturerFor(id)
          .then(manufacturer => new Device(id, manufacturer, model)))
    })));
}

/** @returns {Promise<String>} */
function deviceModelFor(deviceId) {
  return exec(`adb -s ${deviceId} shell getprop ro.product.model`)
    .then(model => model.trim())
}

/** @returns {Promise<String>} */
function deviceManufacturerFor(deviceId) {
  return exec(`adb -s ${deviceId} shell getprop ro.product.manufacturer`)
    .then(manufacturer => manufacturer.trim())
}

/** @returns {Promise<void>} */
function tcpIpFor(deviceId, timeout) {
  return exec(`adb -s ${deviceId} tcpip 5555`, {
      sleep: timeout
    })
    .then(() => waitForDevice(deviceId))
}

/** @returns {Promise<void>} */
function forwardFor(deviceId, localPort, devicePort) {
  return exec(`adb -s ${deviceId} forward tcp:${localPort} tcp:${devicePort}`)
    .then(() => waitForDevice(deviceId))
}

/** @returns {Promise<void>} */
function forwardList(deviceId) {
  return exec(`adb -s ${deviceId} forward --list`)
    .then(() => waitForDevice(deviceId))
}

/** @returns {Promise<void>} */
function connect(deviceId, port) {
  return exec(`adb -s ${deviceId} connect ${LOCAL_IP}:${port}`)
    .then(result => {
      if (!result.startsWith('connected to')) throw Error(`unable to connect to: ${LOCAL_IP}:${port}`);
    })
    .then(() => waitForDevice(deviceId))
}

function setupDevice(deviceId, timeout) {
  let localPort = localPortCounter++;
  return tcpIpFor(deviceId, timeout)
    .then(() => forwardFor(deviceId, localPort, devicePort))
    .then(() => forwardList(deviceId))
    .then(() => connect(deviceId, localPort))
}

function watchForNewDevices(retryEvery) {
  setInterval(() => {
    devices()
      .then(devices => {
        const newDevices = _.differenceBy(devices, preparedDevices, !_.isEqual);
        console.log("Devices connected", newDevices);

        const removedDevices = _.differenceBy(preparedDevices, devices, _.isEqual);
        console.log("Devices removed", removedDevices);

        preparedDevices = _.differenceBy(preparedDevices, removedDevices, _.isEqual);
        console.log("Prepared devices", preparedDevices)

        return Promise.all(newDevices.map(device =>
          //only add to prepared if setup is ok!
          setupDevice(device.id, 250).then(() => preparedDevices.push(device))))
      });

  }, retryEvery);
}

killServer()
  .then(() => startServer())
  .then(() => watchForNewDevices(3000));
