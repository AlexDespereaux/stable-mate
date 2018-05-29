import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  folders = [];
  constructor() { }

  ngOnInit() {
    this.folders = [
      {
        id: '12483975',
        image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(38).jpg'
      },
      {
        id: '49576478',
        image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(19).jpg'
      },
      {
        id: '37402374',
        image:       'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(42).jpg'
      },
      {
        id: '32489324',
        image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(42).jpg'
      },
      {
        id: '03294732',
        image: 'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(8).jpg',
      },
      {
        id: '42384234',
        image:       'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(38).jpg'
      }
    ]
  }

}
