import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
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
