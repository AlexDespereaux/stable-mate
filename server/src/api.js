const express = require('express');
const handlers = require('./handlers');
const router = express.Router();

router.post('/user', express.json(), function (req, res) {
});

router.use(handlers.printRequest);

router.use(handlers.authorise);

router.get('/user', handlers.userType);

router.get('/image/:imageId.png', handlers.imageDownload);

router.get('/image', function (req, res) {

});

router.post('/image', handlers.imageUpload, express.json(), handlers.dataHandler);

module.exports = router;