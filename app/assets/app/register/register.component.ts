import { Component }                              from '@angular/core';
import { Router, ActivatedRoute }                 from '@angular/router';

import { User }                                   from '../_models/index';

import { UserService, AlertService }              from '../_services/index';


@Component({
    templateUrl: './assets/app/register/register.component.html'
})
export class RegisterComponent {
    model: any = {};
    loading = false;
    returnUrl: string;
    
    constructor(
        private router: Router,
        private userService: UserService, 
        private alertService: AlertService){}
    
    ngOnInit(){
    }
    
    register(){
        this.loading = true;
        console.log("Register"); 
        this.userService.create(this.model)
            .subscribe(
                data => {
                    this.alertService.success('Registration successful', true);
                    this.router.navigate(['/login']);
                },
                error => {
                    this.alertService.error(error);
                    this.loading = false;
                }
            );
    }
}
