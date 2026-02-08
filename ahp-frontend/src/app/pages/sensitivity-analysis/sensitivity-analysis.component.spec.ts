import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SensitivityAnalysisComponent } from './sensitivity-analysis.component';
import { AnalysisService } from '../../services/analysis.service';
import { SensitivityResult, RiskLevel } from '../../models/sensitivity.model';

describe('SensitivityAnalysisComponent', () => {
  let component: SensitivityAnalysisComponent;
  let fixture: ComponentFixture<SensitivityAnalysisComponent>;
  let mockAnalysisService: jasmine.SpyObj<AnalysisService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockActivatedRoute: any;

  const mockSensitivityResult: SensitivityResult = {
    criterionId: 1,
    criterionName: 'Price',
    currentWeight: 0.4,
    dataPoints: [
      {
        criterionWeight: 0.0,
        alternativeScores: { 1: 0.3, 2: 0.5, 3: 0.2 },
        ranking: [2, 1, 3]
      },
      {
        criterionWeight: 0.5,
        alternativeScores: { 1: 0.6, 2: 0.3, 3: 0.1 },
        ranking: [1, 2, 3]
      },
      {
        criterionWeight: 1.0,
        alternativeScores: { 1: 0.8, 2: 0.1, 3: 0.1 },
        ranking: [1, 2, 3]
      }
    ],
    criticalPoints: [
      {
        weightThreshold: 0.3,
        beforeWinnerId: 2,
        beforeWinnerName: 'Supplier B',
        afterWinnerId: 1,
        afterWinnerName: 'Supplier A',
        description: 'Ranking changes at 30.0% weight: Supplier A overtakes Supplier B'
      }
    ],
    stabilityMetrics: {
      stabilityScore: 0.85,
      riskLevel: RiskLevel.LOW,
      toleranceRange: 0.25,
      rankingChangeCount: 1
    },
    alternativeNames: { 1: 'Supplier A', 2: 'Supplier B', 3: 'Supplier C' }
  };

  beforeEach(async () => {
    mockAnalysisService = jasmine.createSpyObj('AnalysisService', ['getSensitivityAnalysis']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.callFake((key: string) => {
            const params: { [key: string]: string } = {
              'projectName': 'test-project',
              'analysisId': '1',
              'criterionId': '1'
            };
            return params[key] || null;
          })
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [SensitivityAnalysisComponent],
      providers: [
        { provide: AnalysisService, useValue: mockAnalysisService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SensitivityAnalysisComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load sensitivity analysis on init', () => {
    mockAnalysisService.getSensitivityAnalysis.and.returnValue(of(mockSensitivityResult));

    fixture.detectChanges();

    expect(mockAnalysisService.getSensitivityAnalysis).toHaveBeenCalledWith('test-project', 1, 1);
    expect(component.result()).toEqual(mockSensitivityResult);
    expect(component.loading()).toBeFalse();
    expect(component.error()).toBe('');
  });

  it('should handle error when loading sensitivity analysis', () => {
    const errorResponse = { error: { message: 'Analysis not found' } };
    mockAnalysisService.getSensitivityAnalysis.and.returnValue(throwError(() => errorResponse));

    fixture.detectChanges();

    expect(component.result()).toBeNull();
    expect(component.loading()).toBeFalse();
    expect(component.error()).toBe('Analysis not found');
  });

  it('should set error when route params are missing', () => {
    mockActivatedRoute.snapshot.paramMap.get = jasmine.createSpy('get').and.returnValue(null);

    const newComponent = new SensitivityAnalysisComponent(
      mockActivatedRoute,
      mockRouter,
      mockAnalysisService
    );

    newComponent.ngOnInit();

    expect(newComponent.error()).toBe('Missing analysis or criterion ID');
  });

  it('should return correct risk level class', () => {
    expect(component.getRiskLevelClass(RiskLevel.LOW)).toBe('success');
    expect(component.getRiskLevelClass(RiskLevel.MEDIUM)).toBe('warning');
    expect(component.getRiskLevelClass(RiskLevel.HIGH)).toBe('danger');
  });

  it('should return correct risk icon', () => {
    expect(component.getRiskIcon(RiskLevel.LOW)).toBe('✅');
    expect(component.getRiskIcon(RiskLevel.MEDIUM)).toBe('⚠️');
    expect(component.getRiskIcon(RiskLevel.HIGH)).toBe('🔴');
  });

  it('should navigate back when goBack is called', () => {
    component.projectName.set('test-project');
    component.analysisId.set(1);

    component.goBack();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/analysis', 'test-project', 1]);
  });

  it('should generate correct colors for alternatives', () => {
    const color1 = component.getColorForIndex(0, 1);
    const color2 = component.getColorForIndex(1, 1);

    expect(color1).toContain('rgba');
    expect(color2).toContain('rgba');
    expect(color1).not.toBe(color2);
  });

  it('should destroy chart on component destroy', () => {
    // Create a mock chart
    const mockChart = jasmine.createSpyObj('Chart', ['destroy']);
    (component as any).chart = mockChart;

    component.ngOnDestroy();

    expect(mockChart.destroy).toHaveBeenCalled();
  });
});
