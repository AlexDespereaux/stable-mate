const mysql = require('mysql');
const _ = require('lodash');

const DATABASE = 'annomate';

let connection = function(){
  return mysql.createConnection({
    host: process.env.RDS_HOSTNAME,
    user: process.env.RDS_USERNAME,
    password: process.env.RDS_PASSWORD,
    port: process.env.RDS_PORT,
    database: DATABASE
  })
};

const COLUMN_VALS = ['filename', 'description', 'notes', 'datetime', 'latitude', 'longitude', 'dFov', 'ppm',
  'userId'];

const DUMMY_USER_VAL = {'userId': 1};

exports.insertImageData = function(data){
  let connection = connection();
  connection.connect(function(err) {
    if (err) {
      console.error('Database connection failed: ' + err.stack);
      return err;
    }
    let flattenedData = _.merge({}, data, data.location, DUMMY_USER_VAL);
    let insertVals = _.pick(flattenedData, COLUMN_VALS);
    let sql = mysql.format('INSERT INTO images SET ?;', insertVals);
    connection.query(sql, function (error, results, fields) {
      if (error) throw error;
      console.log(results);
      connection.end();
      return results.insertId;
    })
  });
};