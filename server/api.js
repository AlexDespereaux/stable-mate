const express = require('express');
const AWS = require('aws-sdk');
const router = express.Router();

let printRequestHeaders = function(req) {
    console.log("\nReceived headers");
    console.log("----------------");

    for (let key in req.headers) {
        console.log(key + ": " + req.headers[key]);
    }

    console.log("");
};

let binaryUploadHandler = function(req, res) {
    console.log("\n\nBinary Upload Request from: " + req.ip);
    printRequestHeaders(req);

  let s3 = new AWS.S3({apiVersion: '2006-03-01'});
    let filename = req.headers["file-name"];
    console.log("Started binary upload of: " + filename);
    req.pipe(uploadFromStream(s3));
    req.on('end', function() {
        res.sendStatus(200);
    });
};

function uploadFromStream(s3) {
  let pass = new stream.PassThrough();

  let params = {
    Body: pass,
    Bucket: "annomate",
    Key: "exampleobject",
    Metadata: {
      "metadata1": "value1",
      "metadata2": "value2"
    }
  };
  s3.putObject(params, function(err, data) {
    if (err)
      console.log(err, err.stack); // an error occurred
    else
      console.log(data);
  });

  return pass;
}

router.get('/', function(req, res){
  res.send('hello world');
});

router.post('/image', binaryUploadHandler);

module.exports = router;
