import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize, throwError } from 'rxjs';
import { LoadingService } from '../services/loading.service';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  const loadingService = inject(LoadingService);

  // Show loading for non-GET requests or specific GET requests
  const shouldShowLoading = req.method !== 'GET' || req.url.includes('/analyses');
  if (shouldShowLoading) {
    loadingService.show();
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side error
        if (error.status === 0) {
          errorMessage = 'Unable to connect to the server. Please check your internet connection.';
        } else if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.error?.errors) {
          // Validation errors
          const validationErrors = error.error.errors;
          errorMessage = Object.values(validationErrors).join(', ');
        } else {
          switch (error.status) {
            case 400:
              errorMessage = 'Invalid request. Please check your input.';
              break;
            case 401:
              errorMessage = 'Your session has expired. Please log in again.';
              break;
            case 403:
              errorMessage = 'You do not have permission to perform this action.';
              break;
            case 404:
              errorMessage = 'The requested resource was not found.';
              break;
            case 409:
              errorMessage = 'A conflict occurred. The resource may already exist.';
              break;
            case 500:
              errorMessage = 'A server error occurred. Please try again later.';
              break;
            default:
              errorMessage = `Error ${error.status}: ${error.statusText}`;
          }
        }
      }

      // Show error toast (but not for 401 on login page)
      if (!(error.status === 401 && req.url.includes('/auth/login'))) {
        toastService.error(errorMessage);
      }

      return throwError(() => error);
    }),
    finalize(() => {
      if (shouldShowLoading) {
        loadingService.hide();
      }
    })
  );
};
