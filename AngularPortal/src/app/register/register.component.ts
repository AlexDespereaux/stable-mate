import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, EmailValidator } from '@angular/forms';
import { first } from 'rxjs/operators';


@Component({ templateUrl: 'register.component.html' })
export class RegisterComponent implements OnInit {
    registerForm: FormGroup;
    loading = false;
    submitted = false;

    constructor(
        private formBuilder: FormBuilder,
        private router: Router) { }

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            firstName: ['', Validators.required],
            lastName: ['', Validators.required],
            // tslint:disable-next-line:max-line-length
            email: ['', Validators.compose([Validators.required, Validators.pattern('^[^\s@]+@[^\s@]+\.[^\s@]{2,}$'), Validators.minLength(1)])],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.registerForm.controls; }

    redirect() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.registerForm.invalid) {
            return;
        } else{
            this.router.navigate(['new-user']); 

        }
    }
}
