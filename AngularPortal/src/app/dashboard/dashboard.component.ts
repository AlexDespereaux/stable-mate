import { Component, OnInit } from '@angular/core';
import { Router, NavigationExtras } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  // will be initialized later
   students = new Array<{id: string, class: string, images: Array<string>}>();

  constructor(private router: Router) { }

  ngOnInit() {
    this.students = [{
      id: '12458764',
      class: 'class',
      images: [
        '../../assets/images/img1.JPG',
        '../../assets/images/img2.JPG',
        '../../assets/images/img3.JPG',
        '../../assets/images/img4.JPG'
      ]
    }];
    console.log(this.students[0].class);
  }

  redirect(studentId: number, id: number) {
    const navigationExtras: NavigationExtras = {
      queryParams: {
       class: id + 1,
       images: this.students[studentId].images
      }
    };
    this.router.navigate(['displayClass'], navigationExtras);
  }
}

