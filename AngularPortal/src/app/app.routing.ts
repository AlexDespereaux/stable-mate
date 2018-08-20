import { Routes, RouterModule } from '@angular/router';

import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegisterComponent } from "./register/register.component";
import { StudentComponent } from './student/student.component';
import { NewUserComponent } from './new-user/new-user.component';
import { LoginComponent } from './login/login.component';
import { DisplayImageComponent } from './student/display-class/display-image/display-image.component';
import { DisplayClassComponent } from './student/display-class/display-class.component';

const appRoutes: Routes = [
  {
    path: 'student/:id', component: StudentComponent,
    children: [
      {
        path: 'displayClass/:id', component: DisplayClassComponent, children: [
          { path: 'image', component: DisplayImageComponent }
        ]
      },
    ]
  },
  { path: 'admin-dashboard', component: AdminDashboardComponent },
  { path: 'new-user', component: NewUserComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }
];

export const routing = RouterModule.forRoot(appRoutes);
