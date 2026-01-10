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
  showPassword = signal(false);

  constructor(private authService: AuthService, private router: Router) {}

  togglePasswordVisibility(): void {
    this.showPassword.set(!this.showPassword());
  }

  register(): void {
    if (!this.form.nickname || !this.form.name || !this.form.surename || !this.form.email || !this.form.password) {
      this.error.set('Please fill in all fields');
      return;
    }

    if (this.form.password.length < 8) {
      this.error.set('Password must be at least 8 characters');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set('');
    this.success.set('');

    this.authService.register(this.form).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        this.success.set('Registration successful! Please check your email to activate your account.');
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        console.error('Registration error:', error);
        if (error.status === 409) {
          this.error.set('Nickname or email already exists');
        } else if (error.status === 400) {
          this.error.set('Validation failed. Ensure all fields are valid and password has at least 8 characters.');
        } else {
          this.error.set('Registration failed. Please try again.');
        }
        this.isSubmitting.set(false);
      }
    });
  }
}
