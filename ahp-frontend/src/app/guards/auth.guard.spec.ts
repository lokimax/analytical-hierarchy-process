import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { createAuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const mockAuthService = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    const mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should allow access when user is authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);
    const guard = createAuthGuard(authService, router);

    const result = guard({} as any, {} as any);

    expect(result).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should deny access and navigate to login when user is not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);
    const guard = createAuthGuard(authService, router);

    const result = guard({} as any, {} as any);

    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call isAuthenticated method', () => {
    authService.isAuthenticated.and.returnValue(true);
    const guard = createAuthGuard(authService, router);

    guard({} as any, {} as any);

    expect(authService.isAuthenticated).toHaveBeenCalled();
  });

  it('should handle multiple guard invocations', () => {
    authService.isAuthenticated.and.returnValue(false);
    const guard = createAuthGuard(authService, router);

    // First invocation
    const result1 = guard({} as any, {} as any);
    expect(result1).toBe(false);

    // Second invocation
    authService.isAuthenticated.and.returnValue(true);
    const result2 = guard({} as any, {} as any);
    expect(result2).toBe(true);

    expect(authService.isAuthenticated).toHaveBeenCalledTimes(2);
  });

  it('should navigate to correct login route', () => {
    authService.isAuthenticated.and.returnValue(false);
    const guard = createAuthGuard(authService, router);

    guard({} as any, {} as any);

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
