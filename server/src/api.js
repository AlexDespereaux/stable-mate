const express = require('express');
const handlers = require('./handlers');
const router = express.Router();
const db = require('./database');

router.post('/user', express.json(), function (req, res) {
});

router.use(handlers.printRequest);

router.use(handlers.authorise);

router.get('/user', handlers.userType);

router.get('/image/:imageId.png', handlers.imageDownload);

router.get('/image', function (req, res) {
  let userPromises = [db.getUserType(req), db.getUserId(req)];
  Promise.all(userPromises)
    .then((userInfo) => {
      res.status(200).send(userInfo);
    });
});

router.post('/image', handlers.imageUpload, express.json(), handlers.dataHandler);

module.exports = router;