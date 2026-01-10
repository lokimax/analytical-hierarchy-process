import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { Router, provideRouter } from '@angular/router';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['login']);


    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
    expect(component.form.nickname).toBe('');
    expect(component.form.password).toBe('');
    expect(component.isSubmitting()).toBeFalse();
    expect(component.error()).toBe('');
    expect(component.showPassword()).toBeFalse();
  });

  it('should toggle password visibility', () => {
    expect(component.showPassword()).toBeFalse();
    
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBeTrue();
    
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBeFalse();
  });

  it('should show validation error when fields are empty', () => {
    component.form.nickname = '';
    component.form.password = '';

    component.login();

    expect(component.error()).toBe('Please fill in all fields');
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should call AuthService.login and navigate on success', () => {
    component.form.nickname = 'user';
    component.form.password = 'pass';
    authServiceSpy.login.and.returnValue(of({ token: 't', nickname: 'user', email: 'u@example.com' }));

    component.login();

    expect(authServiceSpy.login).toHaveBeenCalledWith({ nickname: 'user', password: 'pass' });
    expect(component.isSubmitting()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should set isSubmitting true while request is in-flight', () => {
    component.form.nickname = 'user';
    component.form.password = 'pass';

    const subject = new Subject<any>();
    authServiceSpy.login.and.returnValue(subject.asObservable());

    component.login();

    // Before emission, should be submitting
    expect(component.isSubmitting()).toBeTrue();

    // Emit success
    subject.next({ token: 't', nickname: 'user', email: 'u@example.com' });
    subject.complete();

    expect(component.isSubmitting()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should show error and stop submitting on failure', () => {
    component.form.nickname = 'user';
    component.form.password = 'pass';

    const subject = new Subject<any>();
    authServiceSpy.login.and.returnValue(subject.asObservable());

    component.login();

    expect(component.isSubmitting()).toBeTrue();

    // Emit error
    subject.error(new Error('bad creds'));

    expect(component.isSubmitting()).toBeFalse();
    expect(component.error()).toBe('Invalid nickname or password');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should clear previous error on new submit', () => {
    component.error.set('Old error');
    component.form.nickname = 'user';
    component.form.password = 'pass';

    const subject = new Subject<any>();
    authServiceSpy.login.and.returnValue(subject.asObservable());

    component.login();
    expect(component.error()).toBe('');

    subject.next({ token: 't', nickname: 'user', email: 'u@example.com' });
    subject.complete();
    expect(component.error()).toBe('');
  });
});
