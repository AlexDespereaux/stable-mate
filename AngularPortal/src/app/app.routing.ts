import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home';
import { LoginComponent } from './login';
import { RegisterComponent } from './register';

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
