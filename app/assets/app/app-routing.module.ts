import { NgModule }                                     from '@angular/core';
import { RouterModule, Routes }                         from '@angular/router';
import { DashboardComponent }                           from './home/dashboard/index';
import { ProjectsComponent }                            from './home/projects/index';
import { ReportsComponent }                             from './home/reports/index';
import { ProjectdetailsComponent }                      from './home/projectdetails/index';
import { PrioritisationComponent }                      from './home/prioritisation/index';
import { LoginComponent }                               from './login/index';
import { RegisterComponent }                            from './register/index';
import { AuthGuard }                                    from './_guards/index';
import { PrioritisationDetailsComponent}                from './home/prioritisationdetails/index';
    
const routes: Routes = [
    { 
      path: '',            
      component: DashboardComponent, canActivate: [AuthGuard],
      children: [
          { path: '', redirectTo: 'projects' },
          { path: 'projects',                                       component: ProjectsComponent },
          { path: 'projectdetails/:name',                           component: ProjectdetailsComponent },
          { path: 'prioritisations/:name',                          component: PrioritisationComponent },
          { path: 'priorisationdetails/:project/:prioritisation',   component: PrioritisationDetailsComponent },
          { path: 'reports/:project/:prioritisation',               component: ReportsComponent },
        ]  
    },
    { path: 'login',       component: LoginComponent },
    { path: 'register',    component: RegisterComponent },
    
    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

@NgModule({
    imports: [ RouterModule.forRoot(routes) ],
    exports: [ RouterModule ]
})
export class AppRoutingModule {}