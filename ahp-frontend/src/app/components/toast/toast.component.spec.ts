import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { Subject } from 'rxjs';
import { ToastComponent } from './toast.component';
import { ToastService } from '../../services/toast.service';

describe('ToastComponent', () => {
  let fixture: ComponentFixture<ToastComponent>;
  let component: ToastComponent;
  let toastService: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    const mockToastService = jasmine.createSpyObj('ToastService', [], {
      toasts$: new Subject().asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [ToastComponent],
      providers: [{ provide: ToastService, useValue: mockToastService }],
    }).compileComponents();

    fixture = TestBed.createComponent(ToastComponent);
    component = fixture.componentInstance;
    toastService = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty toasts array', () => {
    expect(component.toasts).toBeDefined();
    expect(Array.isArray(component.toasts)).toBe(true);
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

  it('should map toast type to title', () => {
    expect(component.getTitle('success')).toBe('Success');
    expect(component.getTitle('error')).toBe('Error');
    expect(component.getTitle('warning')).toBe('Warning');
    expect(component.getTitle('info')).toBe('Info');
  });

  it('should handle unknown toast type', () => {
    const title = component.getTitle('unknown');
    expect(title).toBe('');
  });

  it('should keep toasts array synchronized', () => {
    component.toasts = [];
    expect(component.toasts.length).toBe(0);
    
    component.toasts.push({ id: '1', message: 'New', type: 'info', duration: 1000 });
    expect(component.toasts.length).toBe(1);
  });
});
