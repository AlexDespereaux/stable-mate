import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { DisplayImageComponent } from './display-image/display-image.component';
import { RouterModule, Routes } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { RegisterComponent } from './register/register.component';
import { AdminComponent } from './admin/admin.component';
import { DisplayClassComponent } from './display-class/display-class.component';
import { StarRatingModule } from 'angular-star-rating';
const appRoutes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'displayClass', component: DisplayClassComponent},
  { path: 'admin-dashboard', component: AdminDashboardComponent},
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginPageComponent },
  { path: '',   redirectTo: '/login', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }
];
@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    DashboardComponent,
    DisplayImageComponent,
    PageNotFoundComponent,
    AdminDashboardComponent,
    RegisterComponent,
    AdminComponent,
    DisplayClassComponent
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    StarRatingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }