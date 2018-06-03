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
        'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(38).jpg',
        'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(19).jpg',
        'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(42).jpg',
        'https://mdbootstrap.com/img/Photos/Horizontal/Nature/4-col/img%20(8).jpg'
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

