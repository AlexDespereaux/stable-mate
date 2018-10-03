import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Subscriber } from 'rxjs/Subscriber';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RequestOptions } from '@angular/http';

@Injectable()
export class ImageService {

  private endPoint = "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api";


  constructor(private http: HttpClient) { }

  authenticate(username: string, password: string) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${username}:${password}`)
      })
    };
    console.log(btoa(`${username}:${password}`));
    return this.http.get(`${this.endPoint}/user`, httpOptions);
  }

  getClasses(id) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.get(`${this.endPoint}/image`, httpOptions);
  }
}
