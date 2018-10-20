// database.js
const assert = require('chai').assert;
const sinon = require('sinon');
const mysql = require('mysql');

const database = require('../src/database');

describe('database', function() {
  let mysqlStub;
  let formatStub;

  beforeEach(() => {
    mysqlStub = sinon.stub(mysql, 'createConnection').callsFake(function() {
      return { connect: () => {} }
    });
    formatStub = sinon.stub(mysql, 'format');
  });

  afterEach(() => {
    sinon.restore();
  });

  it('should pass if assert(true)', function () {
    assert(true);
  });

  it('setImageRating should create a sql query to insert an image rating', function (done) {
    database.setImageRating(100, 5)
      .then(() => { console.log('here: ' + formatStub) })
      .catch(error => { console.log('error: ' + error)})
      .then(done());

  });
});