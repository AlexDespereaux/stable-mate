const AWS = require('aws-sdk');
const stream = require('stream');
const _ = require('lodash');
const auth = require('basic-auth');

const db = require('./database');

const s3 = new AWS.S3({apiVersion: '2006-03-01'});

const BUCKET = 'annomate';

exports.printRequest = function (req, res, next) {
  console.log('\nReceived headers');
  console.log(Date());
  console.log('----------------');
  for (let key in req.headers) {
    console.log(key + ': ' + req.headers[key]);
  }
  console.log('');
  next();
};

let uploadFromStream = function () {
  let pass = new stream.PassThrough();
  let params = {
    Body: pass,
    Bucket: BUCKET,
    Key: 'image' + _.padStart(imageCounter++, 6, '0') + '.png'
  };
  s3.upload(params, function (err) {
    if (err)
      console.log(err, err.stack); // an error occurred
  });
  return pass;
};

exports.imageUpload = function (req, res, next) {
  if (req.get('Content-Type') === 'image/png') {
    console.log('Started upload from: ' + req.ip);
    req.pipe(uploadFromStream());
    req.on('end', function () {
      res.status(200).send('Image upload complete!');
    });
  } else {
    next();
  }
};

exports.dataHandler = function (req, res) {
  let status = 400;
  let result = "";
  const REQUIRED_KEYS = ['filename', 'description', 'notes', 'datetime', 'location', 'dFov', 'ppm', 'legend'];
  if (_.every(REQUIRED_KEYS, (key) => req.body[key])) {
    db.getUserId(req)
      .then(userId => {
        let data = _.set(req.body, 'userId', userId);
        return db.insertImageData(data)
      })
      .then(imageId => {
        status = 201;
        result = imageId;
      })
      .catch(error => {
        status = 500;
        result = error;
      })
      .then(() => {
        res.status(status).send(result);
      });
  } else {
    res.status(400).send('Missing information or malformed json');
  }
};

exports.authorise = function (req, res, next) {
  let credentials = auth(req);
  if (credentials && credentials['name'] && credentials['pass']) {
    db.validateUser(credentials).then(() => {
      next();
    }).catch((error) => {
      res.status(401).header('WWW-Authenticate', 'Basic').send(error);
      next('router');
    });
  } else {
    res.status(401).header('WWW-Authenticate', 'Basic').send('Access denied');
    next('router');
  }
};

exports.userType = function (req, res) {
  let status = 400;
  let result = '';
  db.getUserType(req)
    .then(userType => {
      status = 200;
      result = {'userType': userType}
    })
    .catch(error => {
      status = 500;
      result = error
    })
    .then(() => {
      res.status(status).send(result)
    });
};

exports.imageDownload = function (req, res) {
  let s3params = {
    Bucket: BUCKET,
    Key: 'image' + _.padStart(req.params.imageId, 6, '0') + '.png'
  };
  s3.getObject(s3params, function (err, data) {
    if (err) res.status(500).send(err + " " + err.stack);
    else res.status(200).set('Content-Type', 'image/png').send(data.Body);
  });
};