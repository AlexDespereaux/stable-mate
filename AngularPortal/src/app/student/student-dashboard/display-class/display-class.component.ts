import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-display-class',
  templateUrl: './display-class.component.html',
  styleUrls: ['./display-class.component.css']
})
export class DisplayClassComponent implements OnInit {
  images;
  class;
  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.queryParams.subscribe(
      params => {
        this.class = params.class;
        this.images = params.images;
        console.log(params.class);
      },
    (err) => {
      console.log('Something went wrong');
    });
  }

}
