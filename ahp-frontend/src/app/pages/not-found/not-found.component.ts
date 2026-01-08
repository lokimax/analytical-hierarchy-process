import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="row">
      <div class="col-md-6 offset-md-3 text-center">
        <h1 class="display-1 mb-3">404</h1>
        <h2 class="mb-3">Page Not Found</h2>
        <p class="lead mb-4">The page you are looking for doesn't exist.</p>
        <a routerLink="/" class="btn btn-primary btn-lg">Go Home</a>
      </div>
    </div>
  `,
  styles: []
})
export class NotFoundComponent {}

