const http = require('http');
const AdbMonitor = require('./adb-monitor');

const adbMonitor = new AdbMonitor();
adbMonitor.start();

http.createServer((req, res) => {
    if (req.url === '/devices') {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(adbMonitor.preparedDevices));
    }
    else {
        res.writeHead(404);
        res.end();
    }
  })
  .listen(8000);
