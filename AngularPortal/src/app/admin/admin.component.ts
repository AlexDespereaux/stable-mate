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
  };
  adminId;

  constructor(private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(
      params => {
        this.adminId = params.id;
      },
      (err) => {
        console.log('Something went wrong');
      });
    // need to get teachers  classes
    // this.imageService.getClasses(this.adminId).subscribe(
    // (classes) => this.staff.classes = classes;
    // );
  }

  redirect(subject) {
    this.router.navigate(['class', subject], { relativeTo: this.route });
  }

}
