'use strict';

const express = require('express');
const path = require('path');
const api = require('./api');

const PORT = process.env.PORT || 8080;
const HOST = '0.0.0.0';
const SITE_DIR = '../dist';


const app = express();

app.use(express.static(path.join(__dirname, SITE_DIR)));

app.use('/api', api);

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, SITE_DIR, 'index.html'));
});


app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);
