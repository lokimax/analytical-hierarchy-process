import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { SpinnerComponent } from '../../components/spinner/spinner.component';

@Component({
  selector: 'app-activation',
  templateUrl: './activation.component.html',
  styleUrls: ['./activation.component.css'],
  standalone: true,
  imports: [CommonModule, SpinnerComponent]
})
export class ActivationComponent implements OnInit {
  isLoading = signal(false);
  isSuccess = signal(false);
  error = signal<string | null>(null);
  message = signal<string>('');

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.activateAccount(token);
      } else {
        this.error.set('No activation token provided');
      }
    });
  }

  activateAccount(token: string): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.authService.activate(token).subscribe({
      next: (response: any) => {
        this.isLoading.set(false);
        this.isSuccess.set(true);
        
        // Check if account was already activated
        if (response.message && response.message.includes('already activated')) {
          this.message.set('Account already activated! Redirecting to login...');
        } else {
          this.message.set('Account activated successfully! Redirecting to login...');
        }
        
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        this.isLoading.set(false);
        this.error.set('Failed to activate account. The link may have expired or be invalid.');
        console.error('Activation error:', error);
      }
    });
  }
}
