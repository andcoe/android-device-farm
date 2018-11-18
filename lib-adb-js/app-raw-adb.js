const exec = require('child-process-promise').exec;

function startServer() {
  console.log('adb start-server...');
  return exec('adb start-server')
    .then(result => AdbParser.log(result))
    .catch(error => console.error('ERROR: ', error));
}

function devices() {
  return startServer()
    .then(() => {
      console.log('adb devices...');
      return exec('adb devices')
        .then(result => {
          AdbParser.log(result)
          var stdout = result.stdout;
          var stderr = result.stderr;

          if (stderr) throw Error('unexpected stderr:', stderr);

          return AdbParser.parseDevices(stdout);
        })
        .catch(error => console.error('ERROR: ', error));
    });
}

class AdbParser {

  // List of devices attached
  // 0123456789ABCDEF	device
  // 3204486bc15611b5	device
  static parseDevices(stdout) {
    return stdout
      .replace(/List of devices attached\s*/gm, '')
      .replace(/device/gm, '')
      .replace(/\t|\n/gm, ' ')
      .split(' ')
      .filter(device => device);
  }

  static log(result) {
    console.log('<stdout>' + result.stdout + '</stdout>');
    console.log('<stderr>' + result.stderr + '</stderr>');
    console.log('\n------------------------------------')
  }
}

devices().then(devices => console.log("devices: ", devices));
