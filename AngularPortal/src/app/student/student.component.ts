import { Component, OnInit } from '@angular/core';
import { Router, NavigationExtras, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {

  // will be initialized later
  student = {
    id: '12458764',
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
  studentClass = "";
  image = 'assets/logo.png';


  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    
  }

  redirect(classId: number) {
    this.router.navigate(['displayClass', classId], { relativeTo: this.route });
  }

}
