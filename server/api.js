const express = require('express');
const AWS = require('aws-sdk');
const stream = require('stream');
const _ = require('lodash');

const router = express.Router();
const s3 = new AWS.S3({apiVersion: '2006-03-01'});
const BUCKET = 'annomate';

let printRequestHeaders = function(req) {
  console.log("\nReceived headers");
  console.log("----------------");

  for (let key in req.headers) {
    console.log(key + ": " + req.headers[key]);
  }

  console.log("");
};

let uploadHandler = function(req, res) {
  //TODO: mask for token, incorrect upload types

  console.log("\n\nBinary Upload Request from: " + req.ip);
  printRequestHeaders(req);


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
    Bucket: BUCKET,
    Key: "exampleobject",
    Metadata: {
      "metadata1": "value1",
      "metadata2": "value2"
    }
  };
  s3.upload(params, function(err, data) {
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

router.post('/image', uploadHandler);

module.exports = router;
