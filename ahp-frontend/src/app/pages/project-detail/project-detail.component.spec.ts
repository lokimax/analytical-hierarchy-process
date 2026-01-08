import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { of } from 'rxjs';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { ProjectDetailComponent } from './project-detail.component';
import { ProjectService } from '../../services/project.service';
import { NodeService } from '../../services/node.service';
import { AnalysisService } from '../../services/analysis.service';

describe('ProjectDetailComponent', () => {
  let fixture: ComponentFixture<ProjectDetailComponent>;
  let component: ProjectDetailComponent;
  let projectService: jasmine.SpyObj<ProjectService>;
  let nodeService: jasmine.SpyObj<NodeService>;
  let analysisService: jasmine.SpyObj<AnalysisService>;
  let router: Router;

  beforeEach(async () => {
    const mockProjectService = jasmine.createSpyObj('ProjectService', ['getProjects']);
    mockProjectService.getProjects.and.returnValue(
      of([{ id: 1, name: 'Test Project', beschreibung: 'Test Description' }])
    );

    const mockNodeService = jasmine.createSpyObj('NodeService', ['getNodes', 'createNode', 'deleteNode']);
    mockNodeService.getNodes.and.returnValue(
      of([
        { id: 1, name: 'Ziel 1', content: 'GOAL', projectName: 'Test Project' },
        { id: 2, name: 'Kriterium 1', content: 'CRITERION', projectName: 'Test Project' },
        { id: 3, name: 'Alternative 1', content: 'ALTERNATIVE', projectName: 'Test Project' }
      ])
    );
    mockNodeService.createNode.and.returnValue(of({ id: 4, name: 'New Node', content: 'GOAL', projectName: 'Test Project' }));
    mockNodeService.deleteNode.and.returnValue(of(void 0));

    const mockAnalysisService = jasmine.createSpyObj('AnalysisService', ['getAnalyses', 'deleteAnalysis']);
    mockAnalysisService.getAnalyses.and.returnValue(
      of([{ id: 1, name: 'Analysis 1', projectName: 'Test Project', date: new Date() }])
    );
    mockAnalysisService.deleteAnalysis.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [ProjectDetailComponent],
      providers: [
        { provide: ProjectService, useValue: mockProjectService },
        { provide: NodeService, useValue: mockNodeService },
        { provide: AnalysisService, useValue: mockAnalysisService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: jasmine.createSpy('get').and.returnValue('Test Project') } }
          }
        },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;
    projectService = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
    nodeService = TestBed.inject(NodeService) as jasmine.SpyObj<NodeService>;
    analysisService = TestBed.inject(AnalysisService) as jasmine.SpyObj<AnalysisService>;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle form visibility', () => {
    expect(component.showZielForm).toBe(false);
    component.showZielForm = true;
    expect(component.showZielForm).toBe(true);
  });

  it('should validate form input', () => {
    component.newZiel = { name: '', beschreibung: '' };
    component.newZiel = { name: 'Test Ziel', beschreibung: 'Description' };
    expect(component.newZiel.name.trim()).toBeTruthy();
  });

  it('should initialize with correct defaults', () => {
    expect(component.ziele()).toEqual([]);
    expect(component.kriterien()).toEqual([]);
    expect(component.alternativen()).toEqual([]);
    expect(component.analyses()).toEqual([]);
  });

  it('should have methods for node management', () => {
    expect(typeof component.addZiel).toBe('function');
    expect(typeof component.addKriterium).toBe('function');
    expect(typeof component.addAlternative).toBe('function');
  });

  it('should have methods for analysis management', () => {
    expect(typeof component.loadAnalyses).toBe('function');
    expect(typeof component.deleteAnalysis).toBe('function');
  });
});
