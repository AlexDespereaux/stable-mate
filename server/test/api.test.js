// api.js
const request = require('supertest');
const express = require('express');
const api = require('../src/api');
const sinon = require('sinon');

describe('api', function() {
  let app = express();
  let server;

  before(function(done) {
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