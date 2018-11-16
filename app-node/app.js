var http = require('http');
var url  = require('url');

var port = 6555;

http.createServer(function (request, response) {
    response.writeHead(200, {"Content-Type": "text/plain"});
    response.end("" + port);
    if (port === 6556) port = 6555;
    else port++;
  })
  .listen(8000);

// Put a friendly message on the terminal
console.log("Server running at http://127.0.0.1:8000/");
