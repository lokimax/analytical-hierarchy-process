import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { Router, provideRouter } from '@angular/router';
import { HomeComponent } from './home.component';
import { ProjectService } from '../../services/project.service';

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;
  let component: HomeComponent;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let router: Router;

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj<ProjectService>('ProjectService', [
      'getProjects',
      'createProject',
      'updateProject',
      'deleteProject'
    ]);
  jasmine.clock().install();

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create the component', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.showForm()).toBeFalse();
    expect(component.isSubmitting()).toBeFalse();
    expect(component.isLoadingProjects()).toBeFalse();
    expect(component.error()).toBe('');
    expect(component.success()).toBe('');
  });

  it('should load projects on init', () => {
    const mockProjects = [
      { id: 1, name: 'Project 1', beschreibung: 'Test' },
      { id: 2, name: 'Project 2', beschreibung: 'Test' }
    ];
    projectServiceSpy.getProjects.and.returnValue(of(mockProjects));

    fixture.detectChanges();

    expect(component.isLoadingProjects()).toBeFalse();
    expect(component.projects().length).toBe(2);
    expect(component.projects()[0].name).toBe('Project 1');
  });

  it('should handle empty project list', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));

    fixture.detectChanges();

    expect(component.isLoadingProjects()).toBeFalse();
    expect(component.projects().length).toBe(0);
  });

  it('should show loading state while fetching projects', () => {
    const subject = new Subject<any[]>();
    projectServiceSpy.getProjects.and.returnValue(subject.asObservable());

    fixture.detectChanges();
    expect(component.isLoadingProjects()).toBeTrue();

    subject.next([]);
    expect(component.isLoadingProjects()).toBeFalse();
  });

  it('should handle error when loading projects', () => {
    const subject = new Subject<any[]>();
    projectServiceSpy.getProjects.and.returnValue(subject.asObservable());

    fixture.detectChanges();
    expect(component.isLoadingProjects()).toBeTrue();

    subject.error(new Error('Failed to load'));
    expect(component.isLoadingProjects()).toBeFalse();
  });

  it('should toggle form visibility', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    expect(component.showForm()).toBeFalse();
    component.toggleForm();
    expect(component.showForm()).toBeTrue();
    component.toggleForm();
    expect(component.showForm()).toBeFalse();
  });

  it('should clear errors and success when toggling form', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    component.error.set('Some error');
    component.success.set('Some success');
    component.toggleForm();

    expect(component.error()).toBe('');
    expect(component.success()).toBe('');
  });

  it('should validate project name before creating', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    component.projectForm.name = '';
    component.createProject();

    expect(component.error()).toBe('Please enter a project name');
    expect(projectServiceSpy.createProject).not.toHaveBeenCalled();
  });

  it('should trim and validate project name', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    component.projectForm.name = '   ';
    component.createProject();

    expect(component.error()).toBe('Please enter a project name');
    expect(projectServiceSpy.createProject).not.toHaveBeenCalled();
  });

  it('should create project successfully', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    component.projectForm = {
      name: 'New Project',
      beschreibung: 'Test Description',
      clientId: 1
    };

    const createdProject = {
      id: 1,
      name: 'New Project',
      beschreibung: 'Test Description'
    };
    projectServiceSpy.createProject.and.returnValue(of(createdProject));
    projectServiceSpy.getProjects.and.returnValue(of([createdProject]));

    component.createProject();

    expect(component.success()).toContain('created successfully');
    expect(component.isSubmitting()).toBeFalse();
    expect(projectServiceSpy.createProject).toHaveBeenCalledWith({
      name: 'New Project',
      beschreibung: 'Test Description',
      clientId: 1
    });
    expect(projectServiceSpy.getProjects).toHaveBeenCalled();

    jasmine.clock().tick(2000);
    expect(component.showForm()).toBeFalse();
    expect(component.success()).toBe('');
  });

  it('should reset form after successful creation', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    component.projectForm = {
      name: 'New Project',
      beschreibung: 'Test',
      clientId: 1
    };

    const created = { id: 1, name: 'New Project', beschreibung: 'Test' };
    projectServiceSpy.createProject.and.returnValue(of(created));
    projectServiceSpy.getProjects.and.returnValue(of([created]));

    component.createProject();

    expect(component.projectForm.name).toBe('');
    expect(component.projectForm.beschreibung).toBe('');
    expect(component.projectForm.clientId).toBe(1);
  });

  it('should handle creation error', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    component.projectForm = {
      name: 'New Project',
      beschreibung: 'Test',
      clientId: 1
    };

    const subject = new Subject<any>();
    projectServiceSpy.createProject.and.returnValue(subject.asObservable());

    component.createProject();
    expect(component.isSubmitting()).toBeTrue();

    subject.error(new Error('Failed'));
    expect(component.isSubmitting()).toBeFalse();
    expect(component.error()).toBe('Failed to create project. Please try again.');
  });

  it('should navigate to project detail on openProject', () => {
    projectServiceSpy.getProjects.and.returnValue(of([]));
    fixture.detectChanges();

    const project = { id: 1, name: 'Test Project', beschreibung: 'Test' };
    component.openProject(project);

    expect(router.navigate).toHaveBeenCalledWith(['/project', 'Test Project']);
  });
});

  afterEach(() => {
    jasmine.clock().uninstall();
  });
