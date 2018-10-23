import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegisterComponent } from './register/register.component';
import { AdminComponent } from './admin/admin.component';
import { NewUserComponent } from './new-user/new-user.component';
import { ImageService } from "./image.service";
import { HttpModule } from '@angular/http';

import { routing } from './app.routing';
import { ReactiveFormsModule, FormsModule } from '../../node_modules/@angular/forms';
import { AdminDashboardComponent } from "./admin/admin-dashboard/admin-dashboard.component";
import { StudentComponent } from './student/student.component';
import { LoginComponent } from './login';
import { FilterStudentClassesPipe } from './filter-student-classes.pipe';
import { DisplayImageComponent } from './student/display-class/display-image/display-image.component';
import { DisplayClassComponent } from './student/display-class/display-class.component';
import { SortImagesPipe } from './sort-images.pipe';
import { PagerService } from './pager.service';
import { BarRatingModule } from "ngx-bar-rating";
import { HttpClientModule } from '@angular/common/http';
import { StaffClassComponent } from './admin/staff-class/staff-class.component';
import { StudentClassComponent } from './admin/student-class/student-class.component';
import { StudentImageComponent } from './admin/student-image/student-image.component';
import {MatButtonModule, MatCheckboxModule, MatMenuModule} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DisplayImageComponent,
    AdminDashboardComponent,
    PageNotFoundComponent,
    RegisterComponent,
    AdminComponent,
    DisplayClassComponent,
    NewUserComponent,
    StudentComponent,
    FilterStudentClassesPipe,
    SortImagesPipe,
    StaffClassComponent,
    StudentClassComponent,
    StudentImageComponent
  ],
  imports: [
    routing,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    HttpModule ,
    ReactiveFormsModule,
    BarRatingModule,
    BrowserAnimationsModule,
    MatButtonModule, MatCheckboxModule,MatMenuModule],
  providers: [ImageService, PagerService],
  bootstrap: [AppComponent]
})
export class AppModule { }
