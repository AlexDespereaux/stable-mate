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
      '12483975',
      '49576478',
      '37402374',
      '32489324',
      '03294732',
      '42384234'
    ]
  }

}
