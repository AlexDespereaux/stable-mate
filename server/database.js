const mysql = require('mysql');

const DATABASE = 'annomate';

let connection = mysql.createConnection({
  host: process.env.RDS_HOSTNAME,
  user: process.env.RDS_USERNAME,
  password: process.env.RDS_PASSWORD,
  port: process.env.RDS_PORT,
  database: DATABASE
});

const COLUMN_VALS = ['filename', 'description', 'notes', 'datetime', 'latitude', 'longitude', 'dFov', 'ppm',
  'userId'];

const DUMMY_USER_VAL = {'userId': 1};

exports.insertImageData = function(data){
  connection.connect(function(err) {
    if (err) {
      console.error('Database connection failed: ' + err.stack);
      return;
    }
    let flattenedData = _.merge({}, [data, data.location, DUMMY_USER_VAL]);
    let insertVals = _.map(COLUMN_VALS, (columnVal) => _.get(flattenedData, columnVal));
    let sql = mysql.format('INSERT INTO images (??) VALUES ??;', [COLUMN_VALS, insertVals]);
    console.log("sql: " + sql);
    connection.query(sql, function (error, results, fields) {
      if (error) throw error;
      console.log(results);
      console.log(fields);
    })
  });
  connection.end();
  return "insert success";
};