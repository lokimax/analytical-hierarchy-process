import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
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
