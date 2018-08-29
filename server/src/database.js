const mysql = require('mysql');
const _ = require('lodash');

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

const USER_COLUMN_VALUES = ['password','email'];

exports.createUser = function(data) {
  return new Promise((resolve, reject) => {
    connection().then(connection => {
      let insertVals = _.pick(data, USER_COLUMN_VALUES);
      let sql = mysql.format('INSERT INTO users SET ?;', insertVals);
      connection.query(sql, function(err, result) {
        if (error) reject(error);
        connection.end();
        resolve({'userId': result.insertId});
      })
    })
  });
};