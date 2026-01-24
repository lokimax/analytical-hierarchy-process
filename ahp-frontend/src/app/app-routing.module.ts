import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'activate',
    loadComponent: () => import('./pages/activation/activation.component').then(m => m.ActivationComponent)
  },
  {
    path: 'request-password-reset',
    loadComponent: () => import('./pages/request-password-reset/request-password-reset.component').then(m => m.RequestPasswordResetComponent)
  },
  {
    path: 'reset-password',
    loadComponent: () => import('./pages/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
  },
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent),
    canActivate: [() => {
      const authService = inject(AuthService);
      const router = inject(Router);

      if (authService.isAuthenticated()) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }]
  },
  {
    path: 'about',
    loadComponent: () => import('./pages/about/about.component').then(m => m.AboutComponent),
    canActivate: [() => {
      const authService = inject(AuthService);
      const router = inject(Router);

      if (authService.isAuthenticated()) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }]
  },
  {
    path: 'project/:name',
    loadComponent: () => import('./pages/project-detail/project-detail.component').then(m => m.ProjectDetailComponent),
    canActivate: [() => {
      const authService = inject(AuthService);
      const router = inject(Router);

      if (authService.isAuthenticated()) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }]
  },
  {
    path: 'analysis/:name',
    loadComponent: () => import('./pages/analysis/analysis.component').then(m => m.AnalysisComponent),
    canActivate: [() => {
      const authService = inject(AuthService);
      const router = inject(Router);

      if (authService.isAuthenticated()) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }]
  },
  {
    path: 'analysis/:name/:analysisId',
    loadComponent: () => import('./pages/analysis/analysis.component').then(m => m.AnalysisComponent),
    canActivate: [() => {
      const authService = inject(AuthService);
      const router = inject(Router);

      if (authService.isAuthenticated()) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }]
  },
  {
    path: 'projects/:projectName/analyses/:analysisId/sensitivity/:criterionId',
    loadComponent: () => import('./pages/sensitivity-analysis/sensitivity-analysis.component').then(m => m.SensitivityAnalysisComponent),
    canActivate: [() => {
      const authService = inject(AuthService);
      const router = inject(Router);

      if (authService.isAuthenticated()) {
        return true;
      }

      router.navigate(['/login']);
      return false;
    }]
  },
  {
    path: '**',
    loadComponent: () => import('./pages/not-found/not-found.component').then(m => m.NotFoundComponent)
  }
];
