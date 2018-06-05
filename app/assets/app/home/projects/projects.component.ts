import { Component, OnInit }                       from '@angular/core';
import { Router }                                   from '@angular/router';
import { ProjectService }                           from '../../_services/index';
import { Project }                                  from '../../_models/index';


@Component({
    selector: 'projects',
    templateUrl: './assets/app/home/projects/projects.component.html',
    providers: [ProjectService]
})
export class ProjectsComponent implements OnInit {

    model: any = {};
    loading = false;

    constructor(private router: Router, private projectService: ProjectService) { }

    projects: Project[];
    selectedProject: Project;


    ngOnInit() {
        console.log('ProjectsComponent.ngOnInit');
        this.loadProjects();
    }

    loadProjects() {
        console.log('ProjectsComponent.loadProjects');
        this.projectService.getAll().subscribe(projects => { this.projects = projects; });
        // this.projectService.getProjects().then(projects => this.projects = projects);
    }

    add() {
        this.loading = true;
        this.projectService.create(this.model)
            .subscribe(
            data => {
                this.loadProjects();
                this.loading = false;
            },
            error => {
                this.loading = false;
            }
            );
    }

    onSelect(project: Project): void {
        console.log('ProjectsComponent.loadProjects');
        this.selectedProject = project;
    }

    delete() {
        if (this.selectedProject == null) return;
        this.projectService.delete(this.selectedProject.name)
            .subscribe(
            data => {
                this.loadProjects();
                this.selectedProject = null;
                this.loading = false;
            },
            error => {
                this.loading = false;
            }
            );
    }


    gotoStructure(): void {
        this.router.navigate(['/projectdetails', this.selectedProject.name]);
    }

    gotoPrioritisation(): void {
        this.router.navigate(['/prioritisations', this.selectedProject.name]);
    }

    gotoReports(): void {
        this.router.navigate(['/reports', this.selectedProject.name]);
    }
    
}