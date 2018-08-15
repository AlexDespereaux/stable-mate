const fs = require('fs');
const AWS = require('aws-sdk');
const archiver = require('archiver');
const _ = require('lodash');
AWS.config.loadFromPath('./config.json');
// const elasticbeanstalk = new AWS.ElasticBeanstalk({apiVersion: '2010-12-01'});

const filename = 'server-1.0.17.zip';

let output = fs.createWriteStream(filename);
let archive = archiver('zip', {
  zlib: { level: 9 }
});

archive.pipe(output);

const filesToArchive = ['package.json', 'package-lock.json', 'server.js', 'api.js'];

output.on('close', function() {
  console.log(archive.pointer() + ' total bytes');
  console.log('archiver has been finalized and the output file descriptor has closed.');
});

output.on('end', function() {
  console.log('Data has been drained');
});

archive.on('warning', function(err) {
  if (err.code === 'ENOENT') {
  } else {
    throw err;
  }
});

archive.on('error', function(err) {
  throw err;
});

_.forEach(filesToArchive, (file) => {
  archive.file(file, { name: file });
});


archive.directory('dist/', 'dist', {name: 'dist'});

archive.directory('.ebextensions/', '.ebextensions', {name: '.ebextensions'});

archive.finalize();

