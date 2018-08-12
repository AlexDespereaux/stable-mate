import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegisterComponent } from './register/register.component';
import { AdminComponent } from './admin/admin.component';
import { NewUserComponent } from './new-user/new-user.component';
import { routing } from './app.routing';
import { LoginComponent } from './login';
import { HomeComponent } from './home';
import { ReactiveFormsModule, FormsModule } from '../../node_modules/@angular/forms';
import {AdminDashboardComponent} from "./admin/admin-dashboard/admin-dashboard.component";
import { StudentComponent } from './student/student.component';
import { DashboardComponent } from './student/student-dashboard/dashboard.component';
import { DisplayImageComponent } from './student/student-dashboard/display-class/display-image/display-image.component';
import { DisplayClassComponent } from './student/student-dashboard/display-class/display-class.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    DisplayImageComponent,
    AdminDashboardComponent,
    PageNotFoundComponent,
    RegisterComponent,
    AdminComponent,
    DisplayClassComponent,
    NewUserComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    StudentComponent
  ],
  imports: [
    routing,
    BrowserModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
