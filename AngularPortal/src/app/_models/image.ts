export class Image {
  filename: string;
  description: string;
  notes: string;
  datetime: string;
  location: {
    latitude: string,
    longitude: string
  };
  dFov: string;
  ppm: string;
  legend: [{ name: string, text: string }];
  imageId: string;
  rawUrl: string;
  annotatedUrl: string;

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }

}