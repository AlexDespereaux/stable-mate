import { Component, OnInit } from '@angular/core';
import {ImageService} from "../image.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  imagesUrl = [];
  studentId: number;
  classes: number[];


  constructor(private  imageService: ImageService) {
  }


  ngOnInit() {

    this.imageService.getImages();
    this.imagesUrl = [
      'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(38).jpg',
      'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(19).jpg',
      'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(42).jpg',
      'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(8).jpg',
    ];
    this.studentId = 21353581;
    this.classes = [1, 2, 3, 4];
  }

}
