const http = require("http");
const fs = require("fs");
const { URL } = require('url');

const routes = {
    "/v5/challenges": "/openChallenges.json",
    "/v4/srms/schedule": "/srmSchedule.json",
    "/v4/srms/practice/problems": "/practiceProblems.json",
    "/v2/data/srm/problems/10195/rounds": "/problemRounds.json"
}

function modifyResult(url, req, result) {
    if (result.data) {
        result.pageSize = result.data.length;
        result.total = result.data.length;
    }
    result.serverInformation.currentTime = Date.now();

    result.requesterInformation.remoteIP = req.socket.remoteAddress;
    const searchParams = url.searchParams;
    searchParams.forEach((value, name) => {
        result.requesterInformation.receivedParams[name] = value;
    });
}

const requestListener = function (req, res) {
    if (req.method == 'OPTIONS') {
        res.writeHead(204, {
            "Access-Control-Allow-Credentials": true,
            "Access-Control-Allow-Headers": "Authorization,Content-Type,Cache-Control",
            "Access-Control-Allow-Methods": "POST, GET, PUT, DELETE, PATCH, OPTIONS",
            "Access-Control-Allow-Origin": req.headers.origin,
        });
        res.end();
        return;
    }
    const url = new URL(req.protocol + '://' + req.host + req.url);
    const pathname = url.pathname;
    console.log("\n", pathname, " -> ", routes[pathname]);

    if (!routes[pathname]) {
        res.writeHead(404, { "Content-Type": "applciation/json" });
        res.end();
        return;
    }

    const result = JSON.parse(fs.readFileSync(__dirname + routes[pathname]));
    if (pathname.startsWith('/v2')) {
        modifyResult(url, req, result);
    } else if (pathname === '/v5/challenges') {
        result.forEach(item => {
            item.registrationEndDate = new Date(new Date().getTime() + 86400000).toISOString();
        });
    }

    res.writeHead(200, {
        "Access-Control-Allow-Credentials": true,
        "Access-Control-Allow-Headers": "Authorization,Content-Type,Cache-Control",
        "Access-Control-Allow-Methods": "POST, GET, PUT, DELETE, PATCH, OPTIONS",
        "Access-Control-Allow-Origin": req.headers.origin,
        "Content-Type": "application/json"
    });
    res.end(JSON.stringify(result));
};

const server = http.createServer(requestListener);

const port = process.env.PORT || 8081;
server.listen(port);
console.log(`Mock tc-api listen on port ${port}`);