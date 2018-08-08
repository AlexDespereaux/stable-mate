import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {StudentComponent} from "../student/student.component";
import {AdminComponent} from "../admin/admin.component";


@Component({templateUrl: 'login.component.html'})
export class LoginComponent implements OnInit {
    loginForm: FormGroup;
    loading = false;
    submitted = false;
    returnUrl: string;

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router) {}

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    redirect(userType: string = 'student', userId: string = '1'){
      // need to implement regex or the way to detect
      userType = userType.toLocaleLowerCase();

      switch(userType){
        case 'student': this.router.navigate(['StudentComponent'],  { queryParams: { id: userId } }); break;
        case 'admin': this.router.navigate(['AdminComponent'],  { queryParams: { id: userId } }); break;
      }
    }


    verify(){
      //send request to the server to verify depending on type of auth and than redirect them to appropriate page
      this.redirect();
    }
}

