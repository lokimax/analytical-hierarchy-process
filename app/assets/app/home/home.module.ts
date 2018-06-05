import { NgModule }                         from '@angular/core';
import {CommonModule}                       from "@angular/common";
import {FormsModule, ReactiveFormsModule}   from "@angular/forms";
import { DashboardComponent }               from './dashboard/index';

@NgModule({
    imports: [CommonModule, FormsModule, ReactiveFormsModule ],
    declarations: [
        DashboardComponent
    ],
    providers: []
})
export class HomeModule {
}