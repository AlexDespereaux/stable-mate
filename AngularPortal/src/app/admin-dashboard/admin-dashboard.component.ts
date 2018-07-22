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
      image: '../../assets/images/img1.JPG'
    },
    {
      id: '45142115',
      image: '../../assets/images/img2.JPG'
    },
    {
      id: '51487669',
      image: '../../assets/images/img3.JPG',
    },
    {
      id: '15436789',
      image: '../../assets/images/img4.JPG'
    }];
  }

  redirect() {
    this.router.navigate(['./dashboard']);
  }
}
