// api.js
const request = require('supertest');
const express = require('express');
const sinon = require('sinon');
// const assert = require('chai').assert;

const api = require('../src/api');
const handlers = require('../src/handlers');

describe('api', function() {
  let app = express();
  let server;

  before(function(done) {
    sinon.stub(handlers, 'printRequest').callsFake(() => {});
    app.use(api);
    server = app.listen(function(err) {
      if (err) { return done(err); }
      done();
    });
  });

  after(function(){
    server.close();
  });

  afterEach(() => {
    // Restore the default sandbox here
    sinon.restore();
  });

  it('post /image blank/no token', function(done){
    request(app)
      .post('/image')
      .expect(401, done);
  });
});