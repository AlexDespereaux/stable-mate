import { Routes, RouterModule } from '@angular/router';

import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegisterComponent } from "./register/register.component";
import { StudentComponent } from './student/student.component';
import { NewUserComponent } from './new-user/new-user.component';
import { LoginComponent } from './login/login.component';
import { DisplayImageComponent } from './student/display-class/display-image/display-image.component';
import { DisplayClassComponent } from './student/display-class/display-class.component';
import { AdminComponent } from './admin/admin.component';
import { StaffClassComponent } from './admin/staff-class/staff-class.component';
import { StudentClassComponent } from './admin/student-class/student-class.component';
import { StudentImageComponent } from './admin/student-image/student-image.component';

const appRoutes: Routes = [
  { path: 'student/:id', component: StudentComponent },
  { path: 'student/:id/displayClass', component: DisplayClassComponent },
  { path: 'student/:id/displayClass/image/:id', component: DisplayImageComponent },
  // { path: 'admin-dashboard/:id', component: AdminComponent },
  // { path: 'admin-dashboard/:id/class/:id', component: AdminDashboardComponent },
  // { path: 'admin-dashboard/:id/class/:id/student/:id', component: StaffClassComponent },
  // { path: 'admin-dashboard/:id/class/:id/student/:id/class/:id', component: StudentClassComponent },
  // { path: 'admin-dashboard/:id/class/:id/student/:id/class/:id/image/:id', component: StudentImageComponent },

  { path: 'new-user', component: NewUserComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }
];

export const routing = RouterModule.forRoot(appRoutes);
