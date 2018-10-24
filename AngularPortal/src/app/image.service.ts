import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
@Injectable()
export class ImageService {
  username;
  password;
  private endPoint = "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api";

  constructor(private http: HttpClient, private sanitiser: DomSanitizer) { }

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

  getImage(imageId): Observable<SafeUrl> {

     let headers= new HttpHeaders({
        'Authorization': 'Basic ' + btoa(`${this.username}:${this.password}`)
      });
    return this.http
      .get(`${this.endPoint}/image/edit/${imageId}`, {headers, responseType:'blob'})
      .map((value, index) => {
        let urlCreator = window.URL;
        return this.sanitiser.bypassSecurityTrustUrl(urlCreator.createObjectURL(value))
      });
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
