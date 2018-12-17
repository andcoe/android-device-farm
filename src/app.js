const http = require('http');
const AdbMonitor = require('./core/adb-monitor');
const DeviceFarmService = require('./device-farm-service');

const service = new DeviceFarmService(new AdbMonitor());
service.start();

http.createServer((req, res) => {
    if (req.method === 'GET' && req.url === '/devices') {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(service.devices()));
    }
    else if (req.method === 'POST' && req.url === '/lease') {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(service.leaseAny()));
    }
    else {
        res.writeHead(404);
        res.end();
    }
  })
  .listen(8000);
