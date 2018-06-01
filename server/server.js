'use strict';

const express = require('express');
var path = require('path');

// Constants
const PORT = process.env.PORT || 8080;
const HOST = '0.0.0.0';

// App
const app = express();

app.use(express.static(path.join(__dirname, 'dist')));

// app.use('/api', api);

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist/index.html'));
});


app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);
