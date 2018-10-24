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

  getImage(imageId): Observable<Blob> {

     let headers= new HttpHeaders({
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      });
    return this.http.get(`${this.endPoint}/image/edit/${imageId}`, {headers, responseType:'blob'});
  }

  getImageData(imageId) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      })
    };

    return this.http.get(`${this.endPoint}/image/${imageId}`, httpOptions);
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
