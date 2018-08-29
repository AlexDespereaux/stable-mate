const mysql = require('mysql');
const _ = require('lodash');
const bcrypt = require('bcryptjs');

const DATABASE = 'annomate';

let connection = function() {
  return new Promise((resolve, reject) => {
    let connection = mysql.createConnection({
      host: process.env.RDS_HOSTNAME,
      user: process.env.RDS_USERNAME,
      password: process.env.RDS_PASSWORD,
      port: process.env.RDS_PORT,
      database: DATABASE
    });
    connection.connect(function(err){
      if (err) {
        reject(err);
      }
      resolve(connection);
    });
  });
};

const IMAGE_COLUMN_VALUES = ['filename', 'description', 'notes', 'datetime', 'latitude', 'longitude',
  'dFov', 'ppm', 'userId'];

const DUMMY_USER_VALUE = {'userId': 1};

exports.insertImageData = function(data, callback){
  connection().then(connection => {
    let flattenedData = _.merge({}, data, data.location, DUMMY_USER_VALUE);
    let insertVals = _.pick(flattenedData, IMAGE_COLUMN_VALUES);
    let sql = mysql.format('INSERT INTO images SET ?;', insertVals);
    connection.query(sql, function (error, results) {
      if (error) throw error;
      console.log(results);
      connection.end();
      callback({'imageId':results.insertId});
    })
  });
};

exports.createUser = function(data) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      bcrypt.hash(data['password'], 10).then(function(hash) {
        let insertVals = _.merge({}, { password: hash }, _.pick(data, 'email'));
        let sql = mysql.format('INSERT INTO users SET ?;', insertVals);
        connection.query(sql, function(err, result) {
          connection.end();
          if (err) reject(err);
          resolve({'userId': result.insertId});
        });
      });
    })
  });
};

exports.validateUser = function(credentials) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      let sql = mysql.format('SELECT userId, password FROM users WHERE email = ?', credentials.name);
      connection.query(sql, function(err, result) {
        connection.end();
        if (err) reject(err);
        bcrypt.compare(credentials.pass, result[0].password).then(function(res) {
          if (res) resolve(result[0].userId);
          else reject();
        });
      });
    });
  });
};