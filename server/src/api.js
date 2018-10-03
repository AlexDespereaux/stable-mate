const express = require('express');
const handlers = require('./handlers');
const router = express.Router();
const db = require('./database');

router.post('/user', express.json(), function (req, res) {
});

router.use(handlers.printRequest);

router.use(handlers.authorise);

router.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

router.get('/user', handlers.userType);

router.get('/image/:imageId', function(req, res) {res.status(200).send(req.body)});

router.get('/image/:imageId.png', handlers.imageDownload);

router.get('/image', handlers.imageList);

router.post('/image', handlers.imageUpload, express.json(), handlers.dataHandler);

module.exports = router;