import { Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class authGuard {
  constructor(private authService: AuthService, private router: Router) {}
}

export const authGuardFn: CanActivateFn = (route, state) => {
  const authService = new AuthService(null as any, null as any);
  const router = new Router();
  
  if (authService.isAuthenticated()) {
    return true;
  }
  
  router.navigate(['/login']);
  return false;
};

export function createAuthGuard(authService: AuthService, router: Router): CanActivateFn {
  return () => {
    if (authService.isAuthenticated()) {
      return true;
    }
    router.navigate(['/login']);
    return false;
  };
}
