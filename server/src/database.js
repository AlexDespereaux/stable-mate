const mysql = require('mysql');
const _ = require('lodash');
const bcrypt = require('bcryptjs');
const auth = require('basic-auth');

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

exports.getUserId = function(req) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      let credentials = auth(req);
      let sql = mysql.format('SELECT userId FROM users WHERE email = ?', credentials['name']);
      connection.query(sql, function(err, result) {
        connection.end();
        if (err) reject(err);
        resolve(result[0]['userId']);
      });
    });
  });
};

const IMAGE_COLUMN_VALUES = ['filename', 'description', 'notes', 'datetime', 'latitude', 'longitude',
  'dFov', 'ppm', 'userId'];

exports.insertImageData = function(data) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      let insertVals = _.pick(data, IMAGE_COLUMN_VALUES);
      let sql = mysql.format('INSERT INTO images SET ?;', insertVals);
      connection.query(sql, function (error, results) {
        connection.end();
        if (error) reject(error);
        resolve({'imageId':results.insertId});
      });
    });
  });
};

exports.insertLegendData = function(legendItems) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      // legendItems = [[[176, 'a', 'apple'], [176, 'b', 'bee'], [176, 'c', 'cow']]];
      let sql = mysql.format('INSERT INTO legend (imageId, name, text) VALUES ?', legendItems);
      connection.query(sql, function(err, result) {
        connection.end();
        if (err) reject(err);
        resolve(result);
      });
    });
  });
};

exports.createUser = function(data) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      bcrypt.hash(data['password'], 10).then(function(hash) {
        let insertVals = _.merge({}, { password: hash }, _.pick(data, ['email', 'admin']));
        let sql = mysql.format('INSERT INTO users SET ?;', insertVals);
        connection.query(sql, function(err, result) {
          connection.end();
          if (err) reject(err);
          resolve(result.insertId);
        });
      });
    });
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

let userType = function(x) {
  switch (x) {
    case 0:
      return 'user';
    case 1:
      return 'admin';
    default:
      return 'unknown';
  }
};


exports.getUserType = function(req) {
  return new Promise((resolve, reject) => {
    connection()
      .then(connection => {
        let credentials = auth(req);
        let sql = mysql.format('SELECT admin FROM users WHERE email = ?', credentials['name']);
        connection.query(sql, function(err, result) {
          connection.end();
          if (err) reject(err);
          resolve(userType(result[0]['admin']));
        });
      })
      .catch(error => reject(error));
  });
};

exports.getImageIdList = function(userInfo) {
  return new Promise((resolve, reject) => {
    connection()
      .then(connection => {
        let sql = "";
        if (userInfo['userType'] === 'admin') {
          sql = mysql.format('SELECT imageId FROM images');
        } else {
          sql = mysql.format('SELECT imageId FROM images WHERE userId = ?', userInfo['userId']);
        }
        connection.query(sql, function(err, result) {
          connection.end();
          if (err) reject(err);
          resolve(result);
        });
      })
      .catch(error => reject(error));
  });
};

exports.getImageData = function(imageId) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      let sql = mysql.format('SELECT * FROM images WHERE imageId = ?;', imageId);
      connection.query(sql, function (error, results) {
        connection.end();
        if (error) reject(error);
        resolve(results[0]);
      });
    });
  });
};