import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { signal } from '@angular/core';
import { SpinnerComponent } from './spinner.component';
import { LoadingService } from '../../services/loading.service';

describe('SpinnerComponent', () => {
  let fixture: ComponentFixture<SpinnerComponent>;
  let component: SpinnerComponent;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let loadingSignal: any;

  beforeEach(async () => {
    loadingSignal = signal(false);
    loadingServiceSpy = jasmine.createSpyObj<LoadingService>('LoadingService', ['show', 'hide', 'reset'], {
      isLoading: loadingSignal
    });

    await TestBed.configureTestingModule({
      imports: [SpinnerComponent],
      providers: [{ provide: LoadingService, useValue: loadingServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(SpinnerComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should hide loading overlay when isLoading is false', () => {
    loadingSignal.set(false);
    fixture.detectChanges();

    const overlay = fixture.nativeElement.querySelector('.loading-overlay');
    expect(overlay).toBeFalsy();
  });

  it('should show loading overlay when isLoading is true', () => {
    loadingSignal.set(true);
    fixture.detectChanges();

    const overlay = fixture.nativeElement.querySelector('.loading-overlay');
    expect(overlay).toBeTruthy();
  });

  it('should display spinner and text when loading', () => {
    loadingSignal.set(true);
    fixture.detectChanges();

    const spinner = fixture.nativeElement.querySelector('.spinner-border');
    const text = fixture.nativeElement.querySelector('.fw-bold');

    expect(spinner).toBeTruthy();
    expect(text.textContent).toContain('Loading...');
  });
});
