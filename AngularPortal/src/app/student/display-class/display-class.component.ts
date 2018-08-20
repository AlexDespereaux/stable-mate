import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-display-class',
  templateUrl: './display-class.component.html',
  styleUrls: ['./display-class.component.css']
})
export class DisplayClassComponent implements OnInit {
  // this will be replaced by a service call to get images of a ceratin class 
  images = [
    'assets/images/img1.jpg',
    'assets/images/img2.jpg',
    'assets/images/img3.jpg',
    'assets/images/img4.jpg',
    'assets/images/img5.jpg',
  ];
  class;
  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.params.subscribe(
      params => {
        this.class = params.id;
      },
      (err) => {
        console.log('Something went wrong');
      });
  }

}
