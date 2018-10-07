const express = require('express');
const handlers = require('./handlers');
const router = express.Router();
// const db = require('./database');
const cors = require('cors');

router.use(handlers.printRequest);

router.use(cors());

router.use(handlers.authorise);

router.get('/user', handlers.userType);

router.post('/image/:type/:imageId', handlers.imageUpload);

router.get('/image/:type/:imageId', handlers.imageDownload);

router.get('/image/:imageId', handlers.getImageData);

router.get('/image', handlers.imageList);

router.post('/image', express.json(), handlers.uploadImageData);

module.exports = router;