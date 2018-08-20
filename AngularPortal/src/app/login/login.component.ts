import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StudentComponent } from "../student/student.component";
import { AdminComponent } from "../admin/admin.component";


@Component({ 
    templateUrl: 'login.component.html',
    selector: 'app-login',
    styleUrls: ['login.component.css']
})
export class LoginComponent implements OnInit {
    loginForm: FormGroup;
    loading = false;
    submitted = false;
    returnUrl: string;

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router) { }

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required,]
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    redirect(userType: string, userId: string) {
        switch (userType) {
            case 'student': this.router.navigate(['student', userId]); break;
            case 'admin': this.router.navigate(['admin-dashboard'], { queryParams: { id: userId } }); break;
            default: this.router.navigate(['',]); break;
        }
    }


    verify() {
        //send request to the server to verify depending on type of auth and than redirect them to appropriate page
        let user: String = this.loginForm.get('username').value;
        let id = user.replace(/[^0-9]/g, '');

        if (!id) {
            // in further development can be replaced by any alerting library.
            alert('enter a valide ID');
            return;
        }

        (user.includes('students')) ? this.redirect('student', id) :
            (user.includes('staff')) ? this.redirect('admin', id) : this.redirect('', id);
    }
}

