import { Injectable, inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpErrorResponse
} from '@angular/common/http';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Get the auth token from the service
  const authToken = authService.getAuthToken();
  const url = req.url || '';
  const isPublicEndpoint =
    url.includes('/api/clients/register') ||
    url.includes('/api/clients/login') ||
    url.includes('/api/clients/activate');
  
  console.log('Interceptor - URL:', req.url, 'Token:', authToken ? 'YES' : 'NO');

  // Clone the request and add the token if it exists
  if (authToken) {
    console.log('✓ Adding X-Auth-Token header to request:', req.url);
    req = req.clone({
      setHeaders: {
        'X-Auth-Token': authToken
      }
    });
  } else {
    // Avoid noisy logs for public endpoints (register/login/activate)
    if (!isPublicEndpoint) {
      console.log('✗ No token available for request:', req.url);
    }
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle 401/403 responses by logging out
      if (error.status === 401 || error.status === 403) {
        console.log('✗ 401/403 error - logging out');
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};
