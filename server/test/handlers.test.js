// handlers.js
const assert = require('chai').assert;
const sinon = require('sinon');

const handlers = require('../src/handlers');
const db = require('../src/database');

afterEach(() => {
  sinon.restore();
});


describe('handlers', function () {
  let data = {
    "filename": "filename text",
    "description": "description text",
    "notes": "notes text",
    "datetime": 1234567890,
    "location": {
      "latitude": 0.0,
      "longitude": 0.0
    },
    "dFov": 0.0,
    "ppm": 1,
    "legend": [
      {"name": "legend item 1", "text": "legend text 1"},
      {"name": "legend item 2", "text": "legend text 2"},
      {"name": "legend item 3", "text": "legend text 3"}
    ]
  };

  describe('uploadImageData', function () {
    it('should call insertLegendData if legend exists', function () {
      let imageIdObj = {'imageId': 100};
      sinon.stub(db, 'getUserId').callsFake(() => {
        return new Promise(resolve => resolve(6))
      });
      sinon.stub(db, 'insertImageData').callsFake(() => {
        return imageIdObj
      });
      let insertLegendDataStub = sinon.stub(db, 'insertLegendData');
      handlers.uploadImageData({'body': data}, {
        'status': () => {
          return {
            'send': () => {
              assert(insertLegendDataStub.called, 'insertLegendData not called');
            }
          }
        }
      });
    });

    it('should not call insertLegendData if legend is empty', function () {
      let imageIdObj = {'imageId': 100};
      sinon.stub(db, 'getUserId').callsFake(() => {
        return Promise.resolve(6)
      });
      sinon.stub(db, 'insertImageData').callsFake(() => {
        return Promise.resolve(imageIdObj)
      });
      let insertLegendDataStub = sinon.stub(db, 'insertLegendData');
      handlers.uploadImageData({'body': data}, {
        'status': () => {
          return {'send': () => assert(insertLegendDataStub.notCalled, 'insertLegendData not called')}
        }
      });
    });
  });
});