import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  studentClass = "";

  image = 'assets/folder.jpg';
  staff = {
    id: '12321223',
    classes: ['Class 1',
      'Class 2',
      'Class 3',
      'Class 4',
      'Class 5',
      'Class 6',
      'Class 7',
      'Class 8',
      'Class 9',
      'Class 10']
  }
  constructor(private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(
      params => {
        this.staff.id = params.id;
      },
      (err) => {
        console.log('Something went wrong');
      });
  }

  redirect(subject) {
    this.router.navigate(['class', subject], {relativeTo: this.route});
  }

}
