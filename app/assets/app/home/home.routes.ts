import {RouterModule, Routes }              from '@angular/router';
import { DashboardComponent }               from './dashboard/index';


const HOME_ROUTES: Routes = [{
    path: '',
    component: DashboardComponent
}];

export const HomeRouterModule = RouterModule.forChild(HOME_ROUTES);