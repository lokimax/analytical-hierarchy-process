import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { Subject } from 'rxjs';
import { ToastComponent } from './toast.component';
import { ToastService } from '../../services/toast.service';

describe('ToastComponent', () => {
  let fixture: ComponentFixture<ToastComponent>;
  let component: ToastComponent;
  let toastServiceSpy: jasmine.SpyObj<ToastService>;
  let toastSubject: Subject<any>;

  beforeEach(async () => {
    toastSubject = new Subject<any>();
    toastServiceSpy = jasmine.createSpyObj<ToastService>('ToastService', ['show'], {
      toasts$: toastSubject.asObservable()
    });

    jasmine.clock().install();

    await TestBed.configureTestingModule({
      imports: [ToastComponent],
      providers: [{ provide: ToastService, useValue: toastServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(ToastComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jasmine.clock().uninstall();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display toasts from the service', () => {
    toastSubject.next({ id: '1', message: 'Success', type: 'success', duration: 3000 });
    toastSubject.next({ id: '2', message: 'Error', type: 'error', duration: 3000 });
    fixture.detectChanges();

    expect(component.toasts.length).toBe(2);
    expect(component.toasts[0].message).toBe('Success');
    expect(component.toasts[1].message).toBe('Error');
  });

  it('should remove toast by id', () => {
    component.toasts = [
      { id: '1', message: 'Toast 1', type: 'success', duration: 3000 },
      { id: '2', message: 'Toast 2', type: 'error', duration: 3000 }
    ];

    component.removeToast('1');

    expect(component.toasts.length).toBe(1);
    expect(component.toasts[0].id).toBe('2');
  });

  it('should auto-remove toast after duration', () => {
    toastSubject.next({ id: '1', message: 'Auto-remove', type: 'info', duration: 100 });
    fixture.detectChanges();
    expect(component.toasts.length).toBe(1);

    jasmine.clock().tick(150);
    expect(component.toasts.length).toBe(0);
  });

  it('should not auto-remove toast with zero duration', () => {
    toastSubject.next({ id: '1', message: 'Persistent', type: 'warning', duration: 0 });
    fixture.detectChanges();
    expect(component.toasts.length).toBe(1);

    jasmine.clock().tick(150);
    expect(component.toasts.length).toBe(1);
  });

  it('should return correct titles for toast types', () => {
    expect(component.getTitle('success')).toBe('Success');
    expect(component.getTitle('error')).toBe('Error');
    expect(component.getTitle('warning')).toBe('Warning');
    expect(component.getTitle('info')).toBe('Info');
    expect(component.getTitle('unknown')).toBe('');
  });

  it('should apply correct CSS classes for toast types', () => {
    component.toasts = [
      { id: '1', message: 'Success', type: 'success', duration: 3000 },
      { id: '2', message: 'Error', type: 'error', duration: 3000 },
      { id: '3', message: 'Warning', type: 'warning', duration: 3000 },
      { id: '4', message: 'Info', type: 'info', duration: 3000 }
    ];

    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    const toasts = compiled.querySelectorAll('.toast');
    expect(toasts.length).toBe(4);
    expect(toasts[0].classList.contains('bg-success')).toBeTrue();
    expect(toasts[1].classList.contains('bg-danger')).toBeTrue();
    expect(toasts[2].classList.contains('bg-warning')).toBeTrue();
    expect(toasts[3].classList.contains('bg-info')).toBeTrue();
  });
});
