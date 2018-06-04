const express = require('express');
const path = require('path');
const fs = require('fs');
const router = express.Router();

var UPLOAD_PATH = "./uploads/";

function printRequestHeaders(req) {
    console.log("\nReceived headers");
    console.log("----------------");

    for (var key in req.headers) {
        console.log(key + ": " + req.headers[key]);
    }

    console.log("");
}

var binaryUploadHandler = function(req, res) {
    console.log("\n\nBinary Upload Request from: " + req.ip);
    printRequestHeaders(req);

    var filename = req.headers["file-name"];
    console.log("Started binary upload of: " + filename);
    var filepath = path.resolve(UPLOAD_PATH, filename);
    // var out = fs.createWriteStream(filepath, { flags: 'w', encoding: 'binary', fd: null, mode: '644' });
    console.log(req.body.toString())
    req.on('end', function() {
        console.log("Finished binary upload of: " + filename + "\n  in: " + filepath);
        res.sendStatus(200);
    });
};

router.get('/', function(req, res){
  res.send('hello world');
});

router.post('/image', binaryUploadHandler);

module.exports = router;
