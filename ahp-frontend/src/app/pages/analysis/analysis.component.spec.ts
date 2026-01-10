import { TestBed } from '@angular/core/testing';
import { ComponentFixture } from '@angular/core/testing';
import { of } from 'rxjs';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { AnalysisComponent } from './analysis.component';
import { ProjectService } from '../../services/project.service';
import { NodeService } from '../../services/node.service';
import { AnalysisService } from '../../services/analysis.service';

describe('AnalysisComponent', () => {
  let fixture: ComponentFixture<AnalysisComponent>;
  let component: AnalysisComponent;
  let projectService: jasmine.SpyObj<ProjectService>;
  let nodeService: jasmine.SpyObj<NodeService>;
  let analysisService: jasmine.SpyObj<AnalysisService>;

  beforeEach(async () => {
    const mockProjectService = jasmine.createSpyObj('ProjectService', ['getProjects']);
    mockProjectService.getProjects.and.returnValue(
      of([{ id: 1, name: 'Test Project', beschreibung: 'Test' }])
    );

    const mockNodeService = jasmine.createSpyObj('NodeService', ['getNodes']);
    mockNodeService.getNodes.and.returnValue(
      of([
        { id: 1, name: 'Goal', content: 'GOAL', projectName: 'Test Project' },
        { id: 2, name: 'Criteria', content: 'CRITERION', projectName: 'Test Project' },
        { id: 3, name: 'Alternative', content: 'ALTERNATIVE', projectName: 'Test Project' }
      ])
    );

    const mockAnalysisService = jasmine.createSpyObj('AnalysisService', ['getAnalysis', 'createAnalysis']);

    await TestBed.configureTestingModule({
      imports: [AnalysisComponent],
      providers: [
        { provide: ProjectService, useValue: mockProjectService },
        { provide: NodeService, useValue: mockNodeService },
        { provide: AnalysisService, useValue: mockAnalysisService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jasmine.createSpy('get').and.callFake((key: string) => {
                  if (key === 'name') return 'Test Project';
                  return null;
                })
              }
            }
          }
        },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AnalysisComponent);
    component = fixture.componentInstance;
    projectService = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
    nodeService = TestBed.inject(NodeService) as jasmine.SpyObj<NodeService>;
    analysisService = TestBed.inject(AnalysisService) as jasmine.SpyObj<AnalysisService>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with phase 1', () => {
    expect(component.phase()).toBe(1);
  });

  it('should have AHP scale defined', () => {
    expect(component.ahpScale.length).toBe(9);
    expect(component.ahpScale[0].value).toBe(9);
    expect(component.ahpScale[4].value).toBe(1);
  });

  it('should initialize with empty goals, criteria, and alternatives', () => {
    expect(component.goals()).toEqual([]);
    expect(component.criteria()).toEqual([]);
    expect(component.alternatives()).toEqual([]);
  });

  it('should have consistency threshold defined', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should initialize with correct phases', () => {
    expect(component.phase()).toBe(1);
    expect(component.currentCriterionIndex()).toBe(0);
    expect(component.currentComparisonIndex()).toBe(0);
  });

  it('should have proper initial state for results', () => {
    expect(component.criteriaWeights()).toEqual([]);
    expect(component.finalResults()).toEqual([]);
  });
});
