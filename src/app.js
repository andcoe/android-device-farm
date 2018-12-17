const http = require('http');
const AdbMonitor = require('./core/adb-monitor');
const {DeviceFarmService, NoDevicesAvailable} = require('./device-farm-service');

const service = new DeviceFarmService(new AdbMonitor());
service.start();


http.createServer((req, res) => {
    try {
        if (req.method === 'GET' && req.url === '/devices') {
            const devices = JSON.stringify(service.devices())
            res.writeHead(200, {"Content-Type": "application/json"});
            res.end(devices);
        } else if (req.method === 'GET' && req.url === '/leases') {
            const leases = JSON.stringify(service.allLeases())
            res.writeHead(200, {"Content-Type": "application/json"});
            res.end(leases);
        } else if (req.method === 'POST' && req.url === '/leases') {
            const lease = JSON.stringify(service.leaseAny());
            res.writeHead(201, {"Content-Type": "application/json"});
            res.end(lease);
        } else {
            res.writeHead(404);
            res.end();
        }
    } catch (error) {
        switch (error.constructor) {
            case NoDevicesAvailable:
                res.writeHead(400, {"Content-Type": "application/json"});
                res.end(JSON.stringify(error));
                break;
            default:
                console.error('Failed to do something : ', error);
                return res.send(500);
        }

    }
}).listen(8000);