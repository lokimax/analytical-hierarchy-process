import { Component, OnInit }                       from '@angular/core';
import { Router, ActivatedRoute }                  from '@angular/router';

import { User }                                    from '../_models/index';

import { AuthenticationService,  AlertService }    from '../_services/index';

@Component({
    templateUrl: './assets/app/login/login.component.html'
})
export class LoginComponent implements OnInit{
        
    model: any = {};
    loading = false;
    returnUrl: string;
    
    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService,
        private alertService: AlertService) { }
    
    ngOnInit(){
        // reset login status
        this.authenticationService.logout();
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }
    
    login(){
        this.loading = true;
        this.authenticationService.login(this.model.username, this.model.password)
            .subscribe(
                data => {
                    console.log("logged In");
                    this.router.navigate([this.returnUrl]);
                },
                error => {
                    this.alertService.error(error);
                    this.loading = false;
                });
    }
}
