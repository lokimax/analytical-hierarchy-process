import { Component, OnInit }                        from '@angular/core';
import { PrioritisationService }                    from '../../_services/index';
import { ProjectService }                           from '../../_services/index';
import 'rxjs/add/operator/switchMap';
import { Location }                                 from '@angular/common';
import { ActivatedRoute, Params, Router }           from '@angular/router';
import { Prioritisation }                           from '../../_models/index';
import { Project }                                  from '../../_models/index';
import { AlertService }                             from '../../_services/index';

@Component({
    selector: 'priorisation',
    templateUrl: './assets/app/home/prioritisation/prioritisation.component.html'
})
export class PrioritisationComponent implements OnInit {

    model: any = {};
    loading: boolean;

    prioritisations: Prioritisation[];
    selectedPrioritisation: Prioritisation;

    selectedProject: Project;

    constructor(
        private prioritisationService: PrioritisationService,
        private projectService: ProjectService,
        private route: ActivatedRoute,
        private location: Location,
        private router: Router,
        private alertService: AlertService) { }

    ngOnInit() {
        console.log('PrioritisationComponent.ngOnInit');
        this.model.methode = "AHP";
        this.route.params
            .switchMap((params: Params) => this.projectService.getProject(params['name']))
            .subscribe(project => { this.selectedProject = project; this.loadPrioritisation(); });

    }

    loadPrioritisation() {
        if (this.selectedProject) {
            console.log('PrioritisationComponent.loadPrioritisation');
            this.prioritisationService.getAll(this.selectedProject.name).subscribe(prioritisations => { this.prioritisations = prioritisations; });
        }
    }

    onSelect(prioritisation: Prioritisation) {
        if (this.selectedPrioritisation == prioritisation) {
            this.selectedPrioritisation = null;
        } else {
            this.selectedPrioritisation = prioritisation;
        }
    }

    gotoPrioritisation() {
        console.log('PrioritisationComponent.gotoPrioritisation');
        this.router.navigate(['/priorisationdetails', this.selectedProject.name, this.selectedPrioritisation.name]);
    }
    
    gotoReport(){
        console.log('PrioritisationComponent.gotoReport');
        this.router.navigate(['/reports', this.selectedProject.name, this.selectedPrioritisation.name]);
    }

    delete() {
        if (this.selectedPrioritisation == null) return;
        this.prioritisationService.delete(this.selectedProject.name, this.selectedPrioritisation.name)
            .subscribe(
            data => {
                this.loadPrioritisation();
                this.selectedPrioritisation = null;
                this.loading = false;
            },
            error => {
                this.loading = false;
                this.alertService.error(error);
            });
    }

    createPrioritisation() {
        console.log('PrioritisationComponent.createPrioritisation');
        this.prioritisationService.create(this.selectedProject.name, this.model)
            .subscribe(
            data => {
                this.loadPrioritisation()
                this.loading = false;
            },
            error => {
                this.loading = false;
                this.alertService.error(error);
            }
            );
    }
}