import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container">
      <div class="row justify-content-center mt-5">
        <div class="col-md-6">
          <div class="card shadow">
            <div class="card-body p-5">
              <h2 class="card-title text-center mb-4">Login</h2>
              
              <form (ngSubmit)="login()">
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

                <button type="submit" class="btn btn-primary w-100 mb-3" [disabled]="isSubmitting()">
                  <span *ngIf="!isSubmitting()">Login</span>
                  <span *ngIf="isSubmitting()">
                    <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Logging in...
                  </span>
                </button>
              </form>

              <hr>
              
              <p class="text-center mb-0">
                Don't have an account? 
                <a routerLink="/register" class="btn btn-link p-0">Register here</a>
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
export class LoginComponent {
  form = {
    nickname: '',
    password: ''
  };

  isSubmitting = signal(false);
  error = signal('');

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    if (!this.form.nickname || !this.form.password) {
      this.error.set('Please fill in all fields');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');

    this.authService.login(this.form).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Login error:', error);
        this.error.set('Invalid nickname or password');
        this.isSubmitting.set(false);
      }
    });
  }
}
