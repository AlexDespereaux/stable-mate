import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-display-class',
  templateUrl: './display-class.component.html',
  styleUrls: ['./display-class.component.css']
})
export class DisplayClassComponent implements OnInit {
  // this will be replaced by a service call to get images of a ceratin class 
  images = [
    {id:1,image:'assets/images/img1.jpg'},
    {id:1,image:'assets/images/img2.jpg'},
    {id:1,image:'assets/images/img3.jpg'},
    {id:1,image:'assets/images/img4.jpg'},
    {id:1,image:'assets/images/img1.jpg'},
    {id:1,image:'assets/images/img2.jpg'},
  ];
  class;
  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    console.log('from display class student');
    this.route.params.subscribe(
      params => {
        this.class = params.id;
      },
      (err) => {
        console.log('Something went wrong');
      });
  }

  redirect(image) {
      this.router.navigate(['image',image.id], {relativeTo: this.route});
  }

}
