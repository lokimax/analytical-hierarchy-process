import { Component, OnInit }        from '@angular/core';
import { Router }                   from '@angular/router';
import { Project }                  from '../_models/index';

import { ProjectService }           from '../_services/index';

@Component({
    selector: 'my-heroes',
    templateUrl: './assets/app/projects/projects.component.html',
    providers: [ProjectService]
})
export class ProjectsComponent implements OnInit {
  projects: Project[];
  selectedProject: Project;
    
  constructor(private router: Router, private projectService: ProjectService) { }
  
  getProjects(): void {
    this.projectService. getAll()().then(projects => this.projects = projects);
  }
    
  ngOnInit(): void {
    this.getProjects();
  }
  
  onSelect(project: Project): void {
    this.selectedProject = project;
  }
    
  gotoDetail(): void {
    this.router.navigate(['/detail', this.selectedProject.name]);
  }
}