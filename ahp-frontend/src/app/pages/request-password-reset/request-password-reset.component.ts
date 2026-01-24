import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-request-password-reset',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './request-password-reset.component.html'
})
export class RequestPasswordResetComponent {
    email = '';
    isSubmitting = signal(false);
    message = signal('');
    error = signal('');

    constructor(private authService: AuthService) { }

    submit(): void {
        if (!this.email) {
            this.error.set('Please enter your email address');
            return;
        }

        this.isSubmitting.set(true);
        this.message.set('');
        this.error.set('');

        this.authService.requestPasswordReset(this.email).subscribe({
            next: (response) => {
                this.isSubmitting.set(false);
                this.message.set(response.message);
            },
            error: (err) => {
                this.isSubmitting.set(false);
                // Even if error, we might want to show same message or specific error
                this.message.set('If an account exists, a password reset email has been sent.');
            }
        });
    }
}
