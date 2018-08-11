import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import {RegisterComponent} from "./register/register.component";
import {LoginComponent} from "./login";
import { StudentComponent } from './student/student.component';
import { NewUserComponent } from './new-user/new-user.component';
import { DisplayClassComponent } from './student/student-dashboard/display-class/display-class.component';
import { DashboardComponent } from './student/student-dashboard/dashboard.component';

const appRoutes: Routes = [
    { path: 'student-dashboard', component: DashboardComponent },
    { path: 'student/:id', component: StudentComponent},
    { path: 'displayClass', component: DisplayClassComponent},
    { path: 'admin-dashboard', component: AdminDashboardComponent},
    { path: 'new-user', component: NewUserComponent},
    { path: 'register', component: RegisterComponent },
    { path: 'login', component: LoginComponent },
    { path: '',   redirectTo: '/login', pathMatch: 'full' },
    { path: '**', component: PageNotFoundComponent }
  ];

export const routing = RouterModule.forRoot(appRoutes);
