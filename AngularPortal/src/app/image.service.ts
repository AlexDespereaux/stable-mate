import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs/Observable';
import { Subscriber } from 'rxjs/Subscriber';

@Injectable()
export class ImageService {

  private endPoint = "http://localhost:3000";


  constructor(private http: HttpClient) { }

  getImages() {
    // return Observable.create((observer: Subscriber<any>) => {
    //   observer.next(this.images);
    //   observer.complete();
    // });
    // this.http.get(this.endPoint + "/imageList").subscribe(

    //   (result) => {
    //     // return an observable list of images 
    //     console.log(result)
    //   },
    //   (err) =>{
    //     console.log(err)
    //   }
    // );
  }
}
