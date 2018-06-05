import { NgModule }                                     from '@angular/core';
import { BrowserModule }                                from '@angular/platform-browser';
import { FormsModule }                                  from '@angular/forms';
import { HttpModule }                                   from '@angular/http';

import { ChartsModule }                                 from 'ng2-charts/ng2-charts';

import { AppRoutingModule }                             from './app-routing.module';
import { AppComponent }                                 from './app.component';
import { DashboardComponent }                           from './home/dashboard/index';
import { ProjectsComponent }                            from './home/projects/index';
import { ProjectdetailsComponent }                      from './home/projectdetails/index';
import { NodesComponent }                               from './home/nodes/index';
import { ConnectionsComponent }                         from './home/connections/index'
import { PrioritisationComponent }                      from './home/prioritisation/index';
import { PrioritisationDetailsComponent}                from './home/prioritisationdetails/index';

import { ReportsComponent }                             from './home/reports/index';

import { LoginComponent }                               from './login/index';
import { RegisterComponent }                            from './register/index';

import { AuthGuard }                                    from './_guards/index';
import { AlertService }                                 from './_services/index';
import { AuthenticationService }                        from './_services/index';
import { UserService }                                  from './_services/index';
import { ProjectService }                               from './_services/index';
import { NodeService }                                  from './_services/index';
import { ConnectionService }                            from './_services/index';
import { PrioritisationService }                        from './_services/index';
import { ReportService }                                from './_services/index';



import { AlertComponent }                               from './_directives/index';


@NgModule({
    imports: [
        AppRoutingModule,
        BrowserModule,
        FormsModule,
        HttpModule,
        ChartsModule,
    ],
    declarations: [
        AppComponent,
        AlertComponent,
        ConnectionsComponent,
        DashboardComponent,
        LoginComponent,
        NodesComponent,
        ProjectsComponent,
        ProjectdetailsComponent,
        RegisterComponent,
        ReportsComponent,
        PrioritisationComponent,
        PrioritisationDetailsComponent
    ],
    providers: [
        AuthGuard,
        AlertService,
        UserService,
        ProjectService,
        AuthenticationService,
        PrioritisationService,
        NodeService,
        ReportService
    ],
    bootstrap: [AppComponent]
})

export class AppModule { }