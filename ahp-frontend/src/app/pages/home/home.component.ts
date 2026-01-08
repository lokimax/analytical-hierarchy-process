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


