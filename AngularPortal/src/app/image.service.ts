import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable()
export class ImageService {

  private endPoint = "http://localhost:3000";

  constructor(private http: HttpClient) { }

  getImages( ){
    this.http.get(this.endPoint + "/imageList").subscribe(
      (result) => {
        console.log(result)
      },
      (err) =>{
        console.log(err)
      }
    );
  }
}
