import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Subscriber } from 'rxjs/Subscriber';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RequestOptions } from '@angular/http';

@Injectable()
export class ImageService {
  username;
  password;
  private endPoint = "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api";


  constructor(private http: HttpClient) { }

  authenticate(username: string, password: string) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${username}:${password}`)
      })
    };
    return this.http.get(`${this.endPoint}/user`, httpOptions);
  }

  getImageList() {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      })
    };

    return this.http.get(`${this.endPoint}/image`, httpOptions);
  }

  
  getImage(id) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      })
    };
    console.log(id);
    
    
    return this.http.get(`${this.endPoint}/image/edit/${id}`, httpOptions);
  }

  getImageData(id) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      })
    };
    
    return this.http.get(`${this.endPoint}/image/${id}`, httpOptions);
  }

  saveImage(review){
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      })
    };
    
    return this.http.put(`${this.endPoint}/image/review`,review);
  }
}
