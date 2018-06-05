import { Component , OnInit } from '@angular/core';


@Component({
    templateUrl: './assets/app/home/dashboard/dashboard.component.html'
})
export class DashboardComponent implements OnInit{
        
    ngOnInit(){
        console.log('DashboardComponent.ngOnInit');
    }
}