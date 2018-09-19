const express = require('express');
const AWS = require('aws-sdk');
const stream = require('stream');
const _ = require('lodash');
const auth = require('basic-auth');

const db = require('./database');
const router = express.Router();
const s3 = new AWS.S3({apiVersion: '2006-03-01'});

const BUCKET = 'annomate';
const TOKEN = '1F8065545D842E0098709630DBDBEB596D4D6194';

let imageCounter = 0;

let printRequestHeaders = function(req) {
  console.log('\nReceived headers');
  console.log(Date());
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
  };
  s3.upload(params, function(err) {
    if (err)
      console.log(err, err.stack); // an error occurred
    });
  return pass;
};

let imageHandler = function(req, res, next) {
  printRequestHeaders(req);
  if (req.get('token') !== TOKEN) {
    res.status(401).send('Requires security token');
    next('router')
  } else {
    if (req.get('Content-Type') === 'image/png') {
      console.log('Started upload from: ' + req.ip);
      req.pipe(uploadFromStream(s3));
      req.on('end', function() { res.status(200).send('Image upload complete!'); });
    } else {
      next();
    }
  }
};

let dataHandler = function(req, res) {
  const REQUIRED_KEYS = ['filename', 'description', 'notes', 'datetime', 'location', 'dFov', 'ppm', 'legend'];
  if (_.every(REQUIRED_KEYS, (key) => req.body[key])) {
    db.insertImageData(req.body, function(result) { res.status(200).send(result) });
  } else {
    res.status(400).send('Missing information or malformed json')
  }
};

let authorise = function(req, res, next) {
  let credentials = auth(req);
  if (!!credentials && !!credentials['name'] && !!credentials['pass']) {
    db.validateUser(credentials).then(() => {
      next();
    }).catch(() => {
      res.status(401).setHeader('WWW-Authenticate', 'Basic').send('Access denied');
    });
  } else {
    res.status(401).setHeader('WWW-Authenticate', 'Basic').send('Access denied');
  }
};

router.use(authorise);

router.get('/image/:imageId', function(req, res) {
  let s3params = {
    Bucket: BUCKET,
    Key: 'image' + _.padStart(req.params.imageId, 6, '0') + '.png'
  };
  s3.getObject(s3params, function(err, data) {
    if (err) res.status(500).send(err + " " + err.stack);
    else     res.status(200).set('Content-Type','image/png').send(data.Body);
  });
});

router.post('/user', express.json(), function(req, res) {});

router.post('/image', imageHandler, express.json(), dataHandler);

module.exports = router;