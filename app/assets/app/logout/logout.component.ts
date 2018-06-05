import { Component, OnInit }                       from '@angular/core';
import { Router, ActivatedRoute }                  from '@angular/router';
import { User }                                    from '../_models/index';
import { AuthenticationService,  AlertService }    from '../_services/index';

@Component({
    templateUrl: './assets/app/login/logout.component.html'
})
export class LogoutComponent implements OnInit{
        
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
    
    logout(){

    }
}