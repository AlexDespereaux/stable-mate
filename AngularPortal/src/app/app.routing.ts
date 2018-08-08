import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home';
import { DashboardComponent } from './dashboard/dashboard.component';
import { DisplayClassComponent } from './display-class/display-class.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import {RegisterComponent} from "./register/register.component";
import {LoginComponent} from "./login";

const appRoutes: Routes = [
    { path: 'dashboard', component: DashboardComponent },
    { path: 'displayClass', component: DisplayClassComponent},
    { path: 'admin-dashboard', component: AdminDashboardComponent},
    { path: 'register', component: RegisterComponent },
    { path: 'login', component: LoginComponent },
    { path: '',   redirectTo: '/login', pathMatch: 'full' },
    { path: '**', component: PageNotFoundComponent }
  ];

export const routing = RouterModule.forRoot(appRoutes);
