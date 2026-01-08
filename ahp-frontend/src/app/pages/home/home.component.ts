import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="row">
      <div class="col-md-12">
        <h1>Welcome to Analytical Hierarchy Process</h1>
        <p class="lead">This is a modern implementation of the AHP decision-making framework.</p>
        
        <div class="alert alert-info" role="alert">
          <h4 class="alert-heading">What is AHP?</h4>
          <p>The Analytical Hierarchy Process is a structured technique for organizing and analyzing complex decisions.</p>
        </div>

        <button class="btn btn-primary" (click)="startProject()">Start New Project</button>
      </div>
    </div>
  `,
  styles: [`
    h1 {
      color: #333;
      margin-bottom: 1rem;
    }
    
    .lead {
      font-size: 1.25rem;
      color: #666;
    }
  `]
})
export class HomeComponent {
  startProject(): void {
    console.log('Starting new project...');
  }
}
