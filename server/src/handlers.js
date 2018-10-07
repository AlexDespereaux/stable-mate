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

exports.imageUpload = function (req, res) {
  let filename = req.params.type + req.params.imageId + '.png';
  console.log(filename + ' upload starting');
  let upload = function () {
    let pass = new stream.PassThrough();
    let params = {
      Body: pass,
      Bucket: BUCKET,
      Key: filename
    };
    s3.upload(params, function (err) {
      if (err) console.log(err, err.stack); // an error occurred
    });
    return pass;
  };
  req.on('end', function () {
    res.status(201).send(filename);
    console.log(filename + ' upload success');
  });
  req.pipe(upload());
};

exports.uploadImageData = function (req, res) {
  let status = 400;
  let result = "";
  const REQUIRED_KEYS = ['filename', 'description', 'notes', 'datetime', 'location', 'dFov', 'ppm', 'legend'];
  if (_.every(REQUIRED_KEYS, (key) => req.body[key])) {
    db.getUserId(req)
      .then(userId => {
        let imageData = _.merge({},_.set(req.body, 'userId', userId), req.body['location']);
        let legendArrayItem = function(obj) {
          return [userId, obj['name'], obj['text']]
        };
        let legendData = _.map(req.body['legend'], legendArrayItem);
        let userPromises = [db.insertImageData(imageData), db.insertLegendData(legendData) ];
        return Promise.all(userPromises);
      })
      .then(insertResults => {
        status = 201;
        result = insertResults[0];
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
      result = { 'userType': userType }
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
    Key: req.params.type + req.params.imageId + '.png'
  };
  s3.getObject(s3params, function (error, data) {
    if (error) res.status(404).send(error);
    else res.status(200).set('Content-Type', 'image/png').send(data.Body);
  });
};

exports.imageList = function (req, res) {
  let userPromises = [db.getUserType(req), db.getUserId(req)];
  Promise.all(userPromises)
    .then(userInfo => {
      return db.getImageIdList({'userType': userInfo[0], 'userId': userInfo[1]});
    })
    .then(imageList => {
      res.status(200).send(imageList.map(imageIdObj => imageIdObj['imageId']));
    })
    .catch(error => res.status(400).send(error));
};

let userAdmin = function (userType) {
  return new Promise((resolve) => resolve(userType === 'admin'));
};

exports.createAccount = function (req, res) {
    db.getUserType(req)
      .then(userType => { console.log(userType); return userAdmin(userType) })
      .then(userAdmin => { if (userAdmin) { return db.createUser(req.body) } else { throw 'Not an admin' }})
      .then(userId => {console.log(userId); res.status(200).send(userId)})
      .catch(error => res.status(401).send(error));
};

exports.getImageData = function(req, res) {
  db.getImageData(req.params.imageId)
    .then(result => res.status(200).send(result))
    .catch(error => res.status(400).send(error));
};

