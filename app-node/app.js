const http = require('http');
const AdbWatchDog = require('./adb-watchdog');

const adbWatchDog = new AdbWatchDog();
adbWatchDog.start();

http.createServer((req, res) => {
    if (req.url === '/') {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(adbWatchDog.preparedDevices));
    }
    else {
        res.end();
    }
  })
  .listen(8000);
