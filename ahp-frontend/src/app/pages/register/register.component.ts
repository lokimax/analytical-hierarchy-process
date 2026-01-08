import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container">
      <div class="row justify-content-center mt-5">
        <div class="col-md-6">
          <div class="card shadow">
            <div class="card-body p-5">
              <h2 class="card-title text-center mb-4">Register</h2>
              
              <form (ngSubmit)="register()">
                <div class="mb-3">
                  <label for="nickname" class="form-label">Nickname</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="nickname"
                    [(ngModel)]="form.nickname"
                    name="nickname"
                    required
                  >
                </div>

                <div class="mb-3">
                  <label for="name" class="form-label">First Name</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="name"
                    [(ngModel)]="form.name"
                    name="name"
                    required
                  >
                </div>

                <div class="mb-3">
                  <label for="surename" class="form-label">Last Name</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="surename"
                    [(ngModel)]="form.surename"
                    name="surename"
                    required
                  >
                </div>

                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input 
                    type="email" 
                    class="form-control" 
                    id="email"
                    [(ngModel)]="form.email"
                    name="email"
                    required
                  >
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">Password</label>
                  <input 
                    type="password" 
                    class="form-control" 
                    id="password"
                    [(ngModel)]="form.password"
                    name="password"
                    required
                  >
                </div>

                <div *ngIf="error()" class="alert alert-danger" role="alert">
                  {{ error() }}
                </div>

                <div *ngIf="success()" class="alert alert-success" role="alert">
                  {{ success() }}
                </div>

                <button type="submit" class="btn btn-primary w-100 mb-3" [disabled]="isSubmitting()">
                  <span *ngIf="!isSubmitting()">Register</span>
                  <span *ngIf="isSubmitting()">
                    <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Registering...
                  </span>
                </button>
              </form>

              <hr>
              
              <p class="text-center mb-0">
                Already have an account? 
                <a routerLink="/login" class="btn btn-link p-0">Login here</a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .card {
      border-radius: 8px;
      border: none;
    }
  `]
})
export class RegisterComponent {
  form = {
    nickname: '',
    name: '',
    surename: '',
    email: '',
    password: ''
  };

  isSubmitting = signal(false);
  error = signal('');
  success = signal('');

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    if (!this.form.nickname || !this.form.name || !this.form.surename || !this.form.email || !this.form.password) {
      this.error.set('Please fill in all fields');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');
    this.success.set('');

    this.authService.register(this.form).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        this.success.set('Registration successful! Redirecting to login...');
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Registration error:', error);
        if (error.status === 409) {
          this.error.set('Nickname or email already exists');
        } else {
          this.error.set('Registration failed. Please try again.');
        }
        this.isSubmitting.set(false);
      }
    });
  }
}
