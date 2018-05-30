import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  imagesUrl = [];
  studentId: number;
  classes: number[];


  constructor() {
  }


  ngOnInit() {
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
