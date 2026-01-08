import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { Router, provideRouter } from '@angular/router';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('RegisterComponent', () => {
  let fixture: ComponentFixture<RegisterComponent>;
  let component: RegisterComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
    expect(component.form.nickname).toBe('');
    expect(component.form.name).toBe('');
    expect(component.form.surename).toBe('');
    expect(component.form.email).toBe('');
    expect(component.form.password).toBe('');
    expect(component.isSubmitting()).toBeFalse();
    expect(component.error()).toBe('');
    expect(component.success()).toBe('');
  });

  it('should show validation error when fields are empty', () => {
    component.form.nickname = '';
    component.form.name = '';

    component.register();

    expect(component.error()).toBe('Please fill in all fields');
    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('should show validation error when only some fields are filled', () => {
    component.form.nickname = 'user';
    component.form.name = 'John';
    component.form.surename = '';
    component.form.email = 'john@example.com';
    component.form.password = 'pass123';

    component.register();

    expect(component.error()).toBe('Please fill in all fields');
    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('should call AuthService.register with all fields', () => {
    component.form = {
      nickname: 'user',
      name: 'John',
      surename: 'Doe',
      email: 'john@example.com',
      password: 'pass123'
    };
    authServiceSpy.register.and.returnValue(
      of({ nickname: 'user', email: 'john@example.com' })
    );

    jasmine.clock().install();
    component.register();
    jasmine.clock().uninstall();

    expect(authServiceSpy.register).toHaveBeenCalledWith({
      nickname: 'user',
      name: 'John',
      surename: 'Doe',
      email: 'john@example.com',
      password: 'pass123'
    });
  });

  it('should show success message and navigate after successful registration', () => {
    component.form = {
      nickname: 'user',
      name: 'John',
      surename: 'Doe',
      email: 'john@example.com',
      password: 'pass123'
    };
    authServiceSpy.register.and.returnValue(
      of({ nickname: 'user', email: 'john@example.com' })
    );

    jasmine.clock().install();
    component.register();
    expect(component.success()).toBe('Registration successful! Redirecting to login...');
    expect(component.isSubmitting()).toBeFalse();

    jasmine.clock().tick(2000);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    jasmine.clock().uninstall();
  });

  it('should set isSubmitting true while request is in-flight', () => {
    component.form = {
      nickname: 'user',
      name: 'John',
      surename: 'Doe',
      email: 'john@example.com',
      password: 'pass123'
    };

    const subject = new Subject<any>();
    authServiceSpy.register.and.returnValue(subject.asObservable());

    component.register();

    expect(component.isSubmitting()).toBeTrue();
    subject.next({ nickname: 'user', email: 'john@example.com' });
    subject.complete();
    expect(component.isSubmitting()).toBeFalse();
  });

  it('should handle 409 conflict error (duplicate nickname/email)', () => {
    component.form = {
      nickname: 'taken',
      name: 'John',
      surename: 'Doe',
      email: 'taken@example.com',
      password: 'pass123'
    };

    const subject = new Subject<any>();
    authServiceSpy.register.and.returnValue(subject.asObservable());

    component.register();
    expect(component.isSubmitting()).toBeTrue();

    const error409 = new Error('Conflict') as any;
    error409.status = 409;
    subject.error(error409);

    expect(component.isSubmitting()).toBeFalse();
    expect(component.error()).toBe('Nickname or email already exists');
  });

  it('should handle generic registration error', () => {
    component.form = {
      nickname: 'user',
      name: 'John',
      surename: 'Doe',
      email: 'john@example.com',
      password: 'pass123'
    };

    const subject = new Subject<any>();
    authServiceSpy.register.and.returnValue(subject.asObservable());

    component.register();
    expect(component.isSubmitting()).toBeTrue();

    const error = new Error('Server error') as any;
    error.status = 500;
    subject.error(error);

    expect(component.isSubmitting()).toBeFalse();
    expect(component.error()).toBe('Registration failed. Please try again.');
  });

  it('should clear previous errors on new register attempt', () => {
    component.error.set('Old error');
    component.form = {
      nickname: 'user',
      name: 'John',
      surename: 'Doe',
      email: 'john@example.com',
      password: 'pass123'
    };

    const subject = new Subject<any>();
    authServiceSpy.register.and.returnValue(subject.asObservable());

    component.register();
    expect(component.error()).toBe('');
    expect(component.success()).toBe('');
  });
});
