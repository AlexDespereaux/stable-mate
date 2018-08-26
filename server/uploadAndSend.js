const fs = require('fs');
const AWS = require('aws-sdk');
const archiver = require('archiver');
const _ = require('lodash');
AWS.config.loadFromPath('./config.json');
// const elasticbeanstalk = new AWS.ElasticBeanstalk({apiVersion: '2010-12-01'});


let currentDir = './';
let files = fs.readdirSync(currentDir);
let serverZips = _.filter(files, (file) => _.startsWith(file, 'server-'));
let versionRegex = /(server-\d+\.\d+\.)(\d+)\.zip/;
let versionNumbers = _.map(serverZips, (file) => Number(versionRegex.exec(file)[2]));
let maxExistingVersion = _.reduce(versionNumbers, (a, b) => Math.max(a, b));
let newFileName = 'server-1.0.' + ++maxExistingVersion + '.zip';
_.forEach(serverZips, filename => fs.unlink(filename, (err) => {if (err) throw err}));

let output = fs.createWriteStream(newFileName);
let archive = archiver('zip', {
  zlib: { level: 9 }
});

archive.pipe(output);

const filesToArchive = ['package.json', 'package-lock.json', 'src/server.js', 'src/api.js', 'src/database.js'];

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

