const express = require('express');
const AWS = require('aws-sdk');
const stream = require('stream');
const _ = require('lodash');

const router = express.Router();
const s3 = new AWS.S3({apiVersion: '2006-03-01'});
const BUCKET = 'annomate';
const TOKEN = '1F8065545D842E0098709630DBDBEB596D4D6194';

let imageCounter = 0;

let printRequestHeaders = function(req) {
  console.log('\nReceived headers');
  console.log('----------------');

  for (let key in req.headers) {
    console.log(key + ': ' + req.headers[key]);
  }

  console.log('');
};

let uploadHandler = function(req, res) {
  console.log('\n\nUpload Request from: ' + req.ip);
  printRequestHeaders(req);
  if (!req.headers['token'] || req.headers['token'] !== TOKEN) {
    res.sendStatus(401);
  } else {
    let fileExt = '.unk';
    if (req.headers['Content-Type'] === 'image/png')
      fileExt = '.png';
    if (req.headers['Content-Type'] === 'image/jpeg')
      fileExt = '.jpg';
    console.log('Started upload from: ' + req.ip);
    req.pipe(uploadFromStream(s3, fileExt));
    req.on('end', function () {
      res.sendStatus(200);
    });
  }
};

let uploadFromStream = function(s3, fileExt) {
  let pass = new stream.PassThrough();

  let params = {
    Body: pass,
    Bucket: BUCKET,
    Key: 'image' + _.padStart(imageCounter++, 6, '0') + fileExt
    // Metadata: { "metadata1": "value1", "metadata2": "value2" }
  };
  s3.upload(params, function(err, data) {
    if (err)
      console.log(err, err.stack); // an error occurred
    else
      console.log(data);
  });

  return pass;
};

router.get('/', function(req, res){
  res.send('hello world');
});

router.post('/image', uploadHandler);

module.exports = router;
