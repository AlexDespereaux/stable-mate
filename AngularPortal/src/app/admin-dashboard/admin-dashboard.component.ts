import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  folders = [];
  imagesUrl = [ ];
  constructor(private router: Router) { }

  ngOnInit() {
    this.folders = [
    {
      id: '12483975',
      image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(38).jpg'
    },
    {
      id: '45142115',
      image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(19).jpg'
    },
    {
      id: '51487669',
      image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(42).jpg',
    },
    {
      id: '15436789',
      image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(8).jpg'
    }];
  }

  redirect() {
    this.router.navigate(['./dashboard']);
  }
}
