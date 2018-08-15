const express = require('express');
const AWS = require('aws-sdk');
const stream = require('stream');
const _ = require('lodash');
const mysql = require('mysql');


const router = express.Router();
const s3 = new AWS.S3({apiVersion: '2006-03-01'});

const BUCKET = 'annomate';
const TOKEN = '1F8065545D842E0098709630DBDBEB596D4D6194';


const connection = mysql.createConnection({
  host: process.env.RDS_HOSTNAME,
  user: process.env.RDS_USERNAME,
  password: process.env.RDS_PASSWORD,
  port: process.env.RDS_PORT
});

let imageCounter = 0;

let printRequestHeaders = function(req) {
  console.log('\nReceived headers');
  console.log('----------------');
  for (let key in req.headers) {
    console.log(key + ': ' + req.headers[key]);
  }
  console.log('');
};

let uploadFromStream = function(s3) {
  let pass = new stream.PassThrough();

  let params = {
    Body: pass,
    Bucket: BUCKET,
    Key: 'image' + _.padStart(imageCounter++, 6, '0') + '.png'
    // Metadata: { "metadata1": "value1", "metadata2": "value2" }
  };
  s3.upload(params, function(err) {
    if (err)
      console.log(err, err.stack); // an error occurred
  });
  return pass;
};

router.get('/', function(req, res){
  res.send('hello world');
});

router.get('/test', (req, res) => {
  connection.connect(function(err) {
    if (err) {
      console.error('Database connection failed: ' + err.stack);
      return;
    }
    res.send('Connected to database.');
  });

  connection.end();
});

router.post('/image', function(req, res, next) {
  console.log('\n\nUpload Request from: ' + req.ip);
  printRequestHeaders(req);
  if (req.get('token') !== TOKEN) {
    res.sendStatus(401);
    next('router')
  } else {
    if (req.get('Content-Type') === 'image/png') {
      console.log('Started upload from: ' + req.ip);
      req.pipe(uploadFromStream(s3));
      req.on('end', function () { res.sendStatus(200); });
    } else {
      next();
    }
  }
}, express.json(), function (req, res) {
  res.send(req.body);
});

module.exports = router;
