// api.js
const request = require('supertest');
const express = require('express');
const api = require('../src/api');

describe('api', function() {
  let app = express();
  let server;
  let sinonSandbox;
  const TOKEN = '1F8065545D842E0098709630DBDBEB596D4D6194';


  before(function(done) {
    // sinonSandbox = sinon.sandbox.create();
    // sinonSandbox.stub(s3, 'upload')
    //   .returns({
    //
    //   });
    app.use(api);
    server = app.listen(function(err) {
      if (err) { return done(err); }
      done();
    });
  });

  after(function(){
    server.close();
    // sinonSandbox.restore();
  });

  it('GET /', function(done){
    request(app)
      .get('/')
      .expect('x-powered-by', 'Express')
      .expect('Content-Type', /html/)
      .expect(200, done);
  });

  it('POST /image blank/no token', function(done){
    request(app)
      .post('/image')
      .expect(401, done);
  });

  it('POST /image blank/with token', function(done){
    request(app)
      .post('/image')
      .set('token', TOKEN)
      .expect(400, done);
  });

  // it('POST /image png/with token', function(done){
  //   request(app)
  //     .post('/image')
  //     .set('token', TOKEN)
  //     .set('Content-Type', 'image/png')
  //     .expect(200, done);
  // });

});