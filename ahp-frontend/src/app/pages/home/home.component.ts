import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService, Project } from '../../services/project.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="row">
      <div class="col-md-8 offset-md-2">
        <h1 class="mb-4">Welcome to Analytical Hierarchy Process</h1>
        <p class="lead mb-4">This is a modern implementation of the AHP decision-making framework.</p>
        
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title">What is AHP?</h5>
            <p class="card-text">The Analytical Hierarchy Process is a structured technique for organizing and analyzing complex decisions.</p>
          </div>
        </div>

        <div class="mb-4">
          <h3 class="mb-3">My Projects</h3>
          
          <div *ngIf="isLoadingProjects()" class="text-center py-4">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>

          <div *ngIf="!isLoadingProjects() && projects().length === 0" class="alert alert-info">
            <i class="bi bi-info-circle me-2"></i>
            No projects yet. Create your first project to get started!
          </div>

          <div *ngIf="!isLoadingProjects() && projects().length > 0" class="list-group mb-4">
            <button 
              *ngFor="let project of projects()" 
              class="list-group-item list-group-item-action"
              (click)="openProject(project)"
              style="cursor: pointer;">
              <div class="d-flex w-100 justify-content-between align-items-center">
                <div class="flex-grow-1">
                  <h5 class="mb-1">{{ project.name }}</h5>
                  <p class="mb-1 text-muted" *ngIf="project.beschreibung">{{ project.beschreibung }}</p>
                </div>
                <div class="d-flex align-items-center">
                  <small class="text-muted me-3">ID: {{ project.id }}</small>
                  <i class="bi bi-chevron-right"></i>
                </div>
              </div>
            </button>
          </div>
        </div>

        <div *ngIf="!showForm()">
          <button class="btn btn-primary btn-lg" (click)="toggleForm()">Start New Project</button>
        </div>

        <div *ngIf="showForm()" class="card">
          <div class="card-body">
            <h5 class="card-title mb-4">Create New Project</h5>
            
            <div class="mb-3">
              <label for="projectName" class="form-label">Project Name</label>
              <input 
                type="text" 
                class="form-control" 
                id="projectName"
                [(ngModel)]="projectForm.name"
                placeholder="Enter project name"
              >
            </div>

            <div class="mb-3">
              <label for="projectDesc" class="form-label">Description</label>
              <textarea 
                class="form-control" 
                id="projectDesc"
                rows="4"
                [(ngModel)]="projectForm.beschreibung"
                placeholder="Enter project description"
              ></textarea>
            </div>

            <div class="d-grid gap-2 d-sm-flex justify-content-sm-end">
              <button class="btn btn-secondary" (click)="toggleForm()">Cancel</button>
              <button class="btn btn-primary" (click)="createProject()" [disabled]="isSubmitting()">
                <span *ngIf="!isSubmitting()">Create Project</span>
                <span *ngIf="isSubmitting()">
                  <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Creating...
                </span>
              </button>
            </div>

            <div *ngIf="error()" class="alert alert-danger mt-3" role="alert">
              {{ error() }}
            </div>

            <div *ngIf="success()" class="alert alert-success mt-3" role="alert">
              {{ success() }}
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class HomeComponent implements OnInit {
  showForm = signal(false);
  isSubmitting = signal(false);
  isLoadingProjects = signal(false);
  error = signal('');
  success = signal('');
  projects = signal<Project[]>([]);

  projectForm = {
    name: '',
    beschreibung: '',
    clientId: 1
  };

  constructor(private projectService: ProjectService, private router: Router) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  openProject(project: Project): void {
    // Navigate to project detail page
    this.router.navigate(['/project', project.name]);
  }

  loadProjects(): void {
    this.isLoadingProjects.set(true);
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        this.projects.set(projects);
        this.isLoadingProjects.set(false);
      },
      error: (error) => {
        console.error('Error loading projects:', error);
        this.isLoadingProjects.set(false);
      }
    });
  }

  toggleForm(): void {
    this.showForm.update(val => !val);
    this.error.set('');
    this.success.set('');
  }

  createProject(): void {
    if (!this.projectForm.name.trim()) {
      this.error.set('Please enter a project name');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');
    this.success.set('');

    this.projectService.createProject(this.projectForm).subscribe({
      next: (response) => {
        this.success.set(`Project "${response.name}" created successfully!`);
        this.projectForm = { name: '', beschreibung: '', clientId: 1 };
        this.isSubmitting.set(false);
        this.loadProjects(); // Reload projects after creating
        setTimeout(() => {
          this.showForm.set(false);
          this.success.set('');
        }, 2000);
      },
      error: (error) => {
        console.error('Error creating project:', error);
        this.error.set('Failed to create project. Please try again.');
        this.isSubmitting.set(false);
      }
    });
  }
}


