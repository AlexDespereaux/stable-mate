import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-staff-class',
  templateUrl: './staff-class.component.html',
  styleUrls: ['./staff-class.component.css']
})
export class StaffClassComponent implements OnInit {
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
  image = 'assets/folder.jpg';
  constructor(private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
  }

  redirect(subject){
      console.log('staff class component');
      this.router.navigate(['class', subject], {relativeTo: this.route});

  }

}
