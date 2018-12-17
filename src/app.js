const http = require('http');
const AdbMonitor = require('./core/adb-monitor');
const LeaseDao = require('./persistence/lease.dao');
const DeviceFarmService = require('./device-farm.service');
const NoDevicesAvailable = require('./model/exception/no-devices-available.exception');
const LeaseNotFound = require('./model/exception/lease-not-found.exception');

const adbMonitor = new AdbMonitor();
const leaseDao = new LeaseDao();
const service = new DeviceFarmService(adbMonitor, leaseDao);
service.start();

const deleteLeaseIdRegex = new RegExp(("^\/leases\/([^\/]+?)$"));

http.createServer((req, res) => {
    try {
        if (req.method === 'GET' && req.url === '/devices') {
            const devices = JSON.stringify(service.devices());
            res.writeHead(200, {"Content-Type": "application/json"});
            res.end(devices);
        }
        else if (req.method === 'GET' && req.url === '/leases') {
            const leases = JSON.stringify(service.allLeases());
            res.writeHead(200, {"Content-Type": "application/json"});
            res.end(leases);
        }
        else if (req.method === 'POST' && req.url === '/leases') {
            const lease = JSON.stringify(service.leaseAny());
            res.writeHead(201, {"Content-Type": "application/json"});
            res.end(lease);
        }
        else if (req.method === 'DELETE' && deleteLeaseIdRegex.test(req.url)) {
            const leaseId = deleteLeaseIdRegex.exec(req.url)[1];
            service.release(leaseId);
            res.writeHead(204, {"Content-Type": "application/json"});
            res.end();
        }
        else {
            res.writeHead(404);
            res.end();
        }
    } catch (error) {
        switch (error.constructor) {
            case NoDevicesAvailable:
                res.writeHead(400, {"Content-Type": "application/json"});
                res.end(JSON.stringify(error));
                break;
            case LeaseNotFound:
                res.writeHead(404, {"Content-Type": "application/json"});
                res.end(JSON.stringify(error));
                break;
            default:
                console.error('Failed to do something : ', error);
                return res.send(500);
        }

    }
}).listen(8000);