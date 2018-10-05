const express = require('express');
const handlers = require('./handlers');
const router = express.Router();
const db = require('./database');
const cors = require('cors');

router.use(handlers.printRequest);

router.use(cors());

router.use(handlers.authorise);

router.get('/user', handlers.userType);

let userAdmin = function (userType) {
  return new Promise((resolve) => resolve(userType === 'admin'));
};

router.post('/user', express.json(), function (req, res) {
  db.getUserType(req)
    .then(userType => { return userAdmin(userType) })
    .then(userAdmin => { if (userAdmin) { return db.createUser(req.body) } else { throw 'Not an admin' }})
    .then(userId => res.status(200).send(userId))
    .catch(error => res.status(401).send(error));
});

router.post('/image/:type/:imageId', handlers.imageUpload);

router.get('/image/:imageId', function(req, res) {res.status(200).send(req.body)});

router.get('/image/:imageId.png', handlers.imageDownload);

router.get('/image', handlers.imageList);

router.post('/image', express.json(), handlers.dataHandler);

module.exports = router;