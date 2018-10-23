import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StudentComponent } from "../student/student.component";
import { AdminComponent } from "../admin/admin.component";
import { ImageService } from '../image.service';


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
        private router: Router,
        private imageService: ImageService) { }

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
            case 'admin': this.router.navigate(['student', userId]); break;
            case 'user': this.router.navigate(['admin-dashboard', userId]); break;
            default: this.router.navigate(['',]); break;
        }
    }


    verify() {
        //send request to the server to verify depending on type of auth and than redirect them to appropriate page
        let user: string = this.loginForm.get('username').value;
        let id = user.replace(/[^0-9]/g, '');
        let password: string = this.loginForm.get('password').value;
        this.imageService.username = user;
        this.imageService.password = password;
        this.imageService.authenticate(user, password).subscribe(
            res => {
                console.log(res)
                id = user;
                user = res['userType'];

                (user == 'user') ? this.redirect('admin', id) :
                    (user == 'admin') ? this.redirect('admin', id) : this.redirect('', id);
            },
            err => alert('Invalid username or password')
        );
    }
}

