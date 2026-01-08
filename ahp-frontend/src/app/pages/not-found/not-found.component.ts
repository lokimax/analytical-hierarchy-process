import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="row">
      <div class="col-md-6 mx-auto text-center">
        <h1 class="display-1">404</h1>
        <h2>Page Not Found</h2>
        <p class="lead">The page you are looking for doesn't exist.</p>
        <a routerLink="/" class="btn btn-primary">Go Home</a>
      </div>
    </div>
  `,
  styles: [`
    h1 {
      color: #dc3545;
      font-weight: bold;
    }
    
    h2 {
      color: #333;
      margin-bottom: 1rem;
    }
    
    .lead {
      color: #666;
      margin-bottom: 2rem;
    }
  `]
})
export class NotFoundComponent {}
