import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService, LoginRequest, RegisterRequest, AuthResponse } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;
  const apiUrl = '/api/clients';

  beforeEach(() => {
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: Router, useValue: routerSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);

    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  describe('Initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should initialize with no authenticated user', () => {
      expect(service.isAuthenticated()).toBeFalse();
      expect(service.getCurrentUser()()).toBeNull();
    });

    it('should load user from localStorage on initialization', () => {
      const mockUser = {
        nickname: 'testuser',
        email: 'test@example.com',
        name: 'Test User'
      };
      localStorage.setItem('currentUser', JSON.stringify(mockUser));

      // Create new service instance to trigger initialization
      const newService = new AuthService(TestBed.inject(HttpClientTestingModule) as any, router);
      
      expect(newService.getCurrentUser()()).toEqual(mockUser);
      expect(newService.isAuthenticated()).toBeTrue();
    });
  });

  describe('Register', () => {
    it('should register a new user successfully', (done) => {
      const registerRequest: RegisterRequest = {
        nickname: 'newuser',
        email: 'new@example.com',
        name: 'New',
        surename: 'User',
        password: 'password123'
      };

      const mockResponse: AuthResponse = {
        nickname: 'newuser',
        email: 'new@example.com',
        name: 'New'
      };

      service.register(registerRequest).subscribe({
        next: (response) => {
          expect(response).toEqual(mockResponse);
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerRequest);
      req.flush(mockResponse);
    });

    it('should handle registration error', (done) => {
      const registerRequest: RegisterRequest = {
        nickname: 'existinguser',
        email: 'existing@example.com',
        name: 'Existing',
        surename: 'User',
        password: 'password123'
      };

      service.register(registerRequest).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/register`);
      req.flush('User already exists', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('Login', () => {
    it('should login successfully with token', (done) => {
      const loginRequest: LoginRequest = {
        nickname: 'testuser',
        password: 'password123'
      };

      const mockResponse: AuthResponse = {
        token: 'jwt-token-12345',
        nickname: 'testuser',
        email: 'test@example.com',
        name: 'Test User'
      };

      service.login(loginRequest).subscribe({
        next: (response) => {
          expect(response).toEqual(mockResponse);
          expect(service.isAuthenticated()).toBeTrue();
          expect(service.getCurrentUser()()).toEqual({
            nickname: 'testuser',
            email: 'test@example.com',
            name: 'Test User'
          });
          expect(localStorage.getItem('token')).toBe('jwt-token-12345');
          expect(localStorage.getItem('currentUser')).toBeTruthy();
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);
      req.flush(mockResponse);
    });

    it('should login successfully without token', (done) => {
      const loginRequest: LoginRequest = {
        nickname: 'testuser',
        password: 'password123'
      };

      const mockResponse: AuthResponse = {
        nickname: 'testuser',
        email: 'test@example.com'
      };

      service.login(loginRequest).subscribe({
        next: (response) => {
          expect(service.isAuthenticated()).toBeTrue();
          expect(localStorage.getItem('token')).toBeNull();
          done();
        },
        error: done.fail
      });

      const req = httpMock.expectOne(`${apiUrl}/login`);
      req.flush(mockResponse);
    });

    it('should handle login error', (done) => {
      const loginRequest: LoginRequest = {
        nickname: 'wronguser',
        password: 'wrongpass'
      };

      service.login(loginRequest).subscribe({
        next: () => done.fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(401);
          expect(service.isAuthenticated()).toBeFalse();
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/login`);
      req.flush('Invalid credentials', { status: 401, statusText: 'Unauthorized' });
    });

    it('should emit authentication state changes', (done) => {
      const loginRequest: LoginRequest = {
        nickname: 'testuser',
        password: 'password123'
      };

      const mockResponse: AuthResponse = {
        token: 'jwt-token',
        nickname: 'testuser',
        email: 'test@example.com'
      };

      service.isAuthenticated$.subscribe(isAuth => {
        if (isAuth) {
          expect(isAuth).toBeTrue();
          done();
        }
      });

      service.login(loginRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/login`);
      req.flush(mockResponse);
    });
  });

  describe('Logout', () => {
    beforeEach(() => {
      // Setup authenticated state
      const mockUser = {
        nickname: 'testuser',
        email: 'test@example.com'
      };
      localStorage.setItem('currentUser', JSON.stringify(mockUser));
      localStorage.setItem('token', 'jwt-token-12345');
      service.getCurrentUser().set(mockUser);
    });

    it('should logout successfully with token', () => {
      service.logout();

      const req = httpMock.expectOne(`${apiUrl}/logout`);
      expect(req.request.method).toBe('DELETE');
      expect(req.request.headers.get('X-Auth-Token')).toBe('jwt-token-12345');
      req.flush({});

      expect(service.isAuthenticated()).toBeFalse();
      expect(service.getCurrentUser()()).toBeNull();
      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('currentUser')).toBeNull();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should logout even when backend call fails', () => {
      service.logout();

      const req = httpMock.expectOne(`${apiUrl}/logout`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });

      expect(service.isAuthenticated()).toBeFalse();
      expect(localStorage.getItem('token')).toBeNull();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should logout without token', () => {
      localStorage.removeItem('token');
      
      service.logout();

      httpMock.expectNone(`${apiUrl}/logout`);
      expect(service.isAuthenticated()).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('Token Management', () => {
    it('should retrieve token from localStorage', () => {
      localStorage.setItem('token', 'test-token-12345');
      
      const token = service.getAuthToken();
      
      expect(token).toBe('test-token-12345');
    });

    it('should return null when no token exists', () => {
      const token = service.getAuthToken();
      
      expect(token).toBeNull();
    });
  });

  describe('User State Management', () => {
    it('should handle corrupted localStorage data', () => {
      localStorage.setItem('currentUser', 'invalid-json{');
      
      // Create new service instance to trigger initialization
      const newService = new AuthService(TestBed.inject(HttpClientTestingModule) as any, router);
      
      expect(newService.isAuthenticated()).toBeFalse();
      expect(localStorage.getItem('currentUser')).toBeNull();
    });

    it('should return current user signal', () => {
      const userSignal = service.getCurrentUser();
      
      expect(userSignal).toBeTruthy();
      expect(typeof userSignal).toBe('function'); // Signals are functions
    });
  });
});
