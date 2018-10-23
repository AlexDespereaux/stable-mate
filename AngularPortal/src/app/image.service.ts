import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { ResponseContentType } from '@angular/http';
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

  responseType: ResponseContentType.Blob
  getImage(id) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':'image/png',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`),
        'responseType': 'ResponseContentType.Blob'
      })
    };
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

  saveImage(id, rating) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      })
    };

    return this.http.put(`${this.endPoint}/image/${id}/rating/${rating}`, httpOptions);
  }
}
