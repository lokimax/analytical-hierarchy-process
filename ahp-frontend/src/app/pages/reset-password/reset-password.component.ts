import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-reset-password',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './reset-password.component.html'
})
export class ResetPasswordComponent implements OnInit {
    password = '';
    confirmPassword = '';
    token = '';
    isSubmitting = signal(false);
    message = signal('');
    error = signal('');
    success = signal(false);
    showPassword = signal(false);

    togglePasswordVisibility(): void {
        this.showPassword.update(value => !value);
    }

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.token = params['token'];
            if (!this.token) {
                this.error.set('Invalid or missing reset token.');
            }
        });
    }

    submit(): void {
        if (!this.token) {
            this.error.set('Invalid token.');
            return;
        }

        if (!this.password || !this.confirmPassword) {
            this.error.set('Please fill in all fields.');
            return;
        }

        if (this.password !== this.confirmPassword) {
            this.error.set('Passwords do not match.');
            return;
        }

        this.isSubmitting.set(true);
        this.message.set('');
        this.error.set('');

        this.authService.resetPassword(this.token, this.password).subscribe({
            next: (response) => {
                this.isSubmitting.set(false);
                this.success.set(true);
                this.message.set(response.message || 'Password reset successfully.');
                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 3000);
            },
            error: (err) => {
                this.isSubmitting.set(false);
                this.error.set(err.error?.message || 'Failed to reset password. Token might be expired.');
            }
        });
    }
}
