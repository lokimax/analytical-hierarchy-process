import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, Toast } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999">
      <div *ngFor="let toast of toasts"
           class="toast show mb-2"
           role="alert"
           [class.bg-success]="toast.type === 'success'"
           [class.bg-danger]="toast.type === 'error'"
           [class.bg-warning]="toast.type === 'warning'"
           [class.bg-info]="toast.type === 'info'"
           [class.text-white]="toast.type !== 'warning'">
        <div class="toast-header">
          <i class="bi me-2"
             [class.bi-check-circle-fill]="toast.type === 'success'"
             [class.bi-x-circle-fill]="toast.type === 'error'"
             [class.bi-exclamation-triangle-fill]="toast.type === 'warning'"
             [class.bi-info-circle-fill]="toast.type === 'info'"></i>
          <strong class="me-auto">{{ getTitle(toast.type) }}</strong>
          <button type="button" class="btn-close btn-close-white" (click)="removeToast(toast.id)"></button>
        </div>
        <div class="toast-body">
          {{ toast.message }}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .toast {
      min-width: 300px;
      max-width: 500px;
    }
  `]
})
export class ToastComponent implements OnInit {
  toasts: Toast[] = [];

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.toastService.toasts$.subscribe(toast => {
      this.toasts.push(toast);
      
      if (toast.duration && toast.duration > 0) {
        setTimeout(() => {
          this.removeToast(toast.id);
        }, toast.duration);
      }
    });
  }

  removeToast(id: string) {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }

  getTitle(type: string): string {
    switch (type) {
      case 'success': return 'Success';
      case 'error': return 'Error';
      case 'warning': return 'Warning';
      case 'info': return 'Info';
      default: return '';
    }
  }
}
