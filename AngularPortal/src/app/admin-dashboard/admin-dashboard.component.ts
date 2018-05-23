import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  folders = [];
  
  imagesUrl = [
    'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(38).jpg',
    'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(19).jpg',
    'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(42).jpg',
    'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(8).jpg',
  ];

  constructor() { }

  ngOnInit() {
    this.folders = [
      '1248375',
      '4957478',
      '3742374',
      '3289324',
      '0294732',
      '2384234'
    ]
  }

}
