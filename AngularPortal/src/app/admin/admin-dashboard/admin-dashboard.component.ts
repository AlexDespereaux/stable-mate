import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  student = '';
  students = [
    '12483975',
    '45142115',
    '51487669',
    '15436789'
  ];

  image = 'assets/studentIcon.jpg'
  constructor(private router: Router, private route: ActivatedRoute,) { }

  ngOnInit() {
  }

  redirect(student) {
    this.router.navigate(['student', student], {relativeTo: this.route});
  }
}
