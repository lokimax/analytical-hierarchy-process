import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

export interface User {
  id?: number;
  nickname: string;
  email: string;
  name?: string;
}

export interface AuthResponse {
  token?: string;
  nickname: string;
  name?: string;
  surename?: string;
  email: string;
}

export interface LoginRequest {
  nickname: string;
  password: string;
}

export interface RegisterRequest {
  nickname: string;
  email: string;
  name: string;
  surename: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/clients';
  private currentUser = signal<User | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.loadUserFromStorage();
  }

  getCurrentUser() {
    return this.currentUser;
  }

  getAuthToken(): string | null {
    const token = localStorage.getItem('token');
    if (token) {
      console.log('✓ Token retrieved from localStorage:', token.substring(0, 10) + '...');
    } else {
      console.log('✗ No token found in localStorage');
    }
    return token;
  }

  isAuthenticated(): boolean {
    return !!this.currentUser();
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        const user: User = {
          nickname: response.nickname,
          email: response.email,
          name: response.name
        };
        this.currentUser.set(user);
        this.isAuthenticatedSubject.next(true);
        localStorage.setItem('currentUser', JSON.stringify(user));
        if (response.token) {
          localStorage.setItem('token', response.token);
          console.log('✓ Token saved to localStorage:', response.token.substring(0, 10) + '...');
        }
      })
    );
  }

  logout(): void {
    const token = this.getAuthToken();
    if (token) {
      // Call backend logout endpoint
      this.http.delete(`${this.apiUrl}/logout`, {
        headers: { 'X-Auth-Token': token }
      }).subscribe({
        next: () => this.clearAuth(),
        error: () => this.clearAuth() // Clear auth even if logout fails
      });
    } else {
      this.clearAuth();
    }
  }

  private clearAuth(): void {
    this.currentUser.set(null);
    this.isAuthenticatedSubject.next(false);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  activate(token: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/activate?token=${token}`, {}).pipe(
      tap(response => {
        console.log('Activation response:', response);
      })
    );
  }

  requestPasswordReset(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/request-password-reset`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/reset-password`, { token, newPassword });
  }

  private loadUserFromStorage(): void {
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
      try {
        const user = JSON.parse(userJson);
        this.currentUser.set(user);
        this.isAuthenticatedSubject.next(true);
      } catch (error) {
        console.error('Error loading user from storage:', error);
        localStorage.removeItem('currentUser');
      }
    }
  }
}
