import 'rxjs/add/operator/switchMap';
import { Component, Input, OnInit }                 from '@angular/core';
import { Location }                                 from '@angular/common';
import { ActivatedRoute, Params }                   from '@angular/router';

import { Project }                                  from '../../_models/index';
import { ProjectService }                           from '../../_services/index';


@Component({
    selector: 'projects',
    templateUrl: './assets/app/home/projectdetails/projectdetails.component.html'
})
export class ProjectdetailsComponent implements OnInit {

    project: Project;
    editMode: boolean;
    
    constructor(private projectService: ProjectService,
        private route: ActivatedRoute,
        private location: Location) { }

    ngOnInit(): void {
        console.log('ProjectdetailsComponent.ngOnInit');
        this.editMode = false;
        this.route.params
            .switchMap((params: Params) => this.projectService.getProject(params['name']))
            .subscribe(project => this.project = project);
    }
    
    edit(){
        this.editMode = true;
    }
    
    stopEdit(){
        this.editMode = false;
    }
    
    
    update(){
        this.route.params
            .switchMap((params: Params) => this.projectService.update(params['name'], this.project))
            .subscribe(project => this.project = project);
    }
    
    createNewNode(){
    }
}