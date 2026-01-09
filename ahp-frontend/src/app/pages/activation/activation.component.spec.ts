import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, Subject } from 'rxjs';
import { ActivationComponent } from './activation.component';
import { AuthService } from '../../services/auth.service';

describe('ActivationComponent', () => {
  let fixture: ComponentFixture<ActivationComponent>;
  let component: ActivationComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let queryParams$: Subject<any>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['activate']);
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);
    queryParams$ = new Subject<any>();

    await TestBed.configureTestingModule({
      imports: [ActivationComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: { queryParams: queryParams$ } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ActivationComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    if ((jasmine as any).clock().installed) {
      jasmine.clock().uninstall();
    }
  });

  it('should show error when no token is provided', () => {
    authServiceSpy.activate.and.returnValue(of({ message: 'n/a' }));

    fixture.detectChanges();
    queryParams$.next({});

    expect(component.error()).toBe('No activation token provided');
    expect(authServiceSpy.activate).not.toHaveBeenCalled();
  });

  it('should activate account and navigate on success', () => {
    authServiceSpy.activate.and.returnValue(of({ message: 'Account activated successfully' }));

    jasmine.clock().install();
    fixture.detectChanges();
    queryParams$.next({ token: 'abc123' });

    expect(component.isLoading()).toBeFalse();
    expect(component.isSuccess()).toBeTrue();
    expect(component.message()).toBe('Account activated successfully! Redirecting to login...');

    jasmine.clock().tick(3000);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should show already activated message when backend indicates it', () => {
    authServiceSpy.activate.and.returnValue(of({ message: 'Account already activated' }));

    jasmine.clock().install();
    fixture.detectChanges();
    queryParams$.next({ token: 'abc123' });

    expect(component.isSuccess()).toBeTrue();
    expect(component.message()).toBe('Account already activated! Redirecting to login...');

    jasmine.clock().tick(3000);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle activation error', () => {
    const subject = new Subject<any>();
    authServiceSpy.activate.and.returnValue(subject.asObservable());

    fixture.detectChanges();
    queryParams$.next({ token: 'abc123' });

    expect(component.isLoading()).toBeTrue();
    subject.error(new Error('fail'));

    expect(component.isLoading()).toBeFalse();
    expect(component.error()).toBe('Failed to activate account. The link may have expired or be invalid.');
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });
});
