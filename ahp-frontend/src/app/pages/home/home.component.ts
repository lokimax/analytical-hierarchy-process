import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService, Project } from '../../services/project.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  showForm = signal(false);
  isSubmitting = signal(false);
  isLoadingProjects = signal(false);
  error = signal('');
  success = signal('');
  projects = signal<Project[]>([]);
  editingProjectId = signal<number | null>(null);
  deleteConfirmProjectId = signal<number | null>(null);

  projectForm = {
    id: null as number | null,
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

    // If editing, call updateProject instead
    if (this.editingProjectId()) {
      const updateData: Project = {
        name: this.projectForm.name,
        beschreibung: this.projectForm.beschreibung,
        clientId: this.projectForm.clientId
      };
      this.projectService.updateProject(this.editingProjectId()!, updateData).subscribe({
        next: (response) => {
          this.success.set(`Project "${response.name}" updated successfully!`);
          this.projectForm = { id: null, name: '', beschreibung: '', clientId: 1 };
          this.editingProjectId.set(null);
          this.isSubmitting.set(false);
          this.loadProjects(); // Reload projects after updating
          setTimeout(() => {
            this.showForm.set(false);
            this.success.set('');
          }, 2000);
        },
        error: (error) => {
          console.error('Error updating project:', error);
          this.error.set('Failed to update project. Please try again.');
          this.isSubmitting.set(false);
        }
      });
    } else {
      // Creating new project
      const createData: Project = {
        name: this.projectForm.name,
        beschreibung: this.projectForm.beschreibung,
        clientId: this.projectForm.clientId
      };
      this.projectService.createProject(createData).subscribe({
        next: (response) => {
          this.success.set(`Project "${response.name}" created successfully!`);
          this.projectForm = { id: null, name: '', beschreibung: '', clientId: 1 };
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

  editProject(project: Project): void {
    this.editingProjectId.set(project.id || null);
    this.projectForm = {
      id: project.id || null,
      name: project.name,
      beschreibung: project.beschreibung,
      clientId: 1
    };
    this.showForm.set(true);
    this.error.set('');
    this.success.set('');
  }

  confirmDelete(projectId: number): void {
    this.deleteConfirmProjectId.set(projectId);
  }

  cancelDelete(): void {
    this.deleteConfirmProjectId.set(null);
  }

  deleteProject(projectId: number): void {
    this.isSubmitting.set(true);
    this.projectService.deleteProject(projectId).subscribe({
      next: () => {
        this.success.set('Project deleted successfully!');
        this.isSubmitting.set(false);
        this.deleteConfirmProjectId.set(null);
        this.loadProjects(); // Reload projects after deleting
        setTimeout(() => {
          this.success.set('');
        }, 2000);
      },
      error: (error) => {
        console.error('Error deleting project:', error);
        this.error.set('Failed to delete project. Please try again.');
        this.isSubmitting.set(false);
        this.deleteConfirmProjectId.set(null);
      }
    });
  }
}


