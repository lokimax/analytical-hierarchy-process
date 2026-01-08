import { Component, OnInit, signal, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProjectService, Project } from '../../services/project.service';
import { NodeService, Node } from '../../services/node.service';
import { AnalysisService, Analysis } from '../../services/analysis.service';
import { FormsModule } from '@angular/forms';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

@Component({
  selector: 'app-analysis',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="container mt-4">
      <div class="row">
        <div class="col-md-10 offset-md-1">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
              <a [routerLink]="['/project', projectName()]" class="btn btn-outline-secondary mb-2">
                <i class="bi bi-arrow-left"></i> Back to Project
              </a>
              <h1 class="mb-1">AHP Analysis: {{ project()?.name }}</h1>
              <p class="text-muted">Pairwise Comparison</p>
            </div>
          </div>

          <div *ngIf="isLoading()" class="text-center py-5">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>

          <div *ngIf="!isLoading()">
            <!-- Goal Overview -->
            <div class="card mb-4">
              <div class="card-header">
                <h4 class="mb-0"><i class="bi bi-target"></i> Goal</h4>
              </div>
              <div class="card-body">
                <div *ngFor="let goal of goals()" class="mb-2">
                  <h5>{{ goal.name }}</h5>
                  <p class="text-muted mb-0" *ngIf="goal.beschreibung">{{ goal.beschreibung }}</p>
                </div>
              </div>
            </div>

            <!-- Criteria Overview -->
            <div class="card mb-4">
              <div class="card-header">
                <h4 class="mb-0"><i class="bi bi-check2-square"></i> Criteria ({{ criteria().length }})</h4>
              </div>
              <div class="card-body">
                <div class="list-group">
                  <div *ngFor="let crit of criteria(); let i = index" class="list-group-item">
                    <strong>{{ i + 1 }}. {{ crit.name }}</strong>
                    <p class="text-muted mb-0 small" *ngIf="crit.beschreibung">{{ crit.beschreibung }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Alternatives Overview -->
            <div class="card mb-4">
              <div class="card-header">
                <h4 class="mb-0"><i class="bi bi-list-ul"></i> Alternatives ({{ alternatives().length }})</h4>
              </div>
              <div class="card-body">
                <div class="list-group">
                  <div *ngFor="let alt of alternatives(); let i = index" class="list-group-item">
                    <strong>{{ i + 1 }}. {{ alt.name }}</strong>
                    <p class="text-muted mb-0 small" *ngIf="alt.beschreibung">{{ alt.beschreibung }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Progress Indicator -->
            <div class="card mb-4">
              <div class="card-header">
                <h4 class="mb-0"><i class="bi bi-graph-up"></i> Analysis Progress</h4>
              </div>
              <div class="card-body">
                <div class="mb-3">
                  <strong>Phase: {{ getCurrentPhase() }}</strong>
                  <div class="progress mt-2">
                    <div 
                      class="progress-bar" 
                      [style.width.%]="getOverallProgress()"
                      role="progressbar">
                      {{ getOverallProgress() }}%
                    </div>
                  </div>
                </div>
                <ul class="list-unstyled mb-0">
                  <li [class.text-success]="phase() > 1" [class.text-primary]="phase() === 1" [class.text-muted]="phase() < 1">
                    <i class="bi" [class.bi-check-circle-fill]="phase() > 1" [class.bi-circle]="phase() <= 1"></i>
                    Phase 1: Compare Criteria against Goal
                  </li>
                  <li [class.text-success]="phase() > 2" [class.text-primary]="phase() === 2" [class.text-muted]="phase() < 2">
                    <i class="bi" [class.bi-check-circle-fill]="phase() > 2" [class.bi-circle]="phase() <= 2"></i>
                    Phase 2: Compare Alternatives for each Criterion
                  </li>
                  <li [class.text-success]="phase() === 3" [class.text-muted]="phase() < 3">
                    <i class="bi" [class.bi-check-circle-fill]="phase() === 3" [class.bi-circle]="phase() < 3"></i>
                    Phase 3: Calculate Final Results
                  </li>
                </ul>
              </div>
            </div>

            <!-- Results Section -->
            <div *ngIf="phase() === 3" class="card mb-4">
              <div class="card-header bg-success text-white">
                <h4 class="mb-0"><i class="bi bi-trophy"></i> AHP Analysis Results</h4>
              </div>
              <div class="card-body">
                <!-- Criteria Weights -->
                <div class="mb-4">
                  <h5>Criteria Weights (relative to Goal)</h5>
                  <div class="table-responsive">
                    <table class="table table-striped">
                      <thead>
                        <tr>
                          <th>Criterion</th>
                          <th>Weight</th>
                          <th>Percentage</th>
                          <th>Priority</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr *ngFor="let weight of criteriaWeights(); let i = index">
                          <td><strong>{{ weight.criterion.name }}</strong></td>
                          <td>{{ weight.weight.toFixed(4) }}</td>
                          <td>
                            <div class="progress" style="height: 25px;">
                              <div class="progress-bar bg-info" 
                                   [style.width.%]="weight.weight * 100"
                                   role="progressbar">
                                {{ (weight.weight * 100).toFixed(1) }}%
                              </div>
                            </div>
                          </td>
                          <td><span class="badge bg-secondary">{{ i + 1 }}</span></td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>

                <!-- Spider/Radar Chart for Alternatives Comparison -->
                <div class="mb-4">
                  <h5><i class="bi bi-diagram-3"></i> Alternatives Comparison (Spider Chart)</h5>
                  <p class="text-muted small">This spider/radar chart visualizes how each alternative performs across all criteria. Each line represents one alternative.</p>
                  <div class="card">
                    <div class="card-body" style="height: 600px; position: relative;">
                      <canvas #radarChart></canvas>
                    </div>
                  </div>
                </div>

                <!-- Final Alternative Rankings -->
                <div class="mb-4">
                  <h5>Final Alternative Rankings</h5>
                  <div class="table-responsive">
                    <table class="table table-striped table-hover">
                      <thead class="table-primary">
                        <tr>
                          <th>Rank</th>
                          <th>Alternative</th>
                          <th>Overall Score</th>
                          <th>Percentage</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr *ngFor="let result of finalResults(); let i = index"
                            [class.table-success]="i === 0">
                          <td>
                            <h4 class="mb-0">
                              <span class="badge" [class.bg-warning]="i === 0" [class.bg-secondary]="i > 0">
                                {{ i + 1 }}
                              </span>
                            </h4>
                          </td>
                          <td>
                            <strong [class.text-success]="i === 0">{{ result.alternative.name }}</strong>
                            <br>
                            <small class="text-muted" *ngIf="result.alternative.beschreibung">
                              {{ result.alternative.beschreibung }}
                            </small>
                          </td>
                          <td>{{ result.score.toFixed(4) }}</td>
                          <td>
                            <div class="progress" style="height: 30px;">
                              <div class="progress-bar" 
                                   [class.bg-success]="i === 0"
                                   [class.bg-primary]="i > 0"
                                   [style.width.%]="result.score * 100"
                                   role="progressbar">
                                {{ (result.score * 100).toFixed(1) }}%
                              </div>
                            </div>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>

                <!-- Alternative Scores per Criterion -->
                <div class="mb-4">
                  <h5>Alternative Scores per Criterion (Details)</h5>
                  <div *ngFor="let criterion of criteria()" class="mb-3">
                    <h6 class="text-primary">{{ criterion.name }}</h6>
                    <div class="table-responsive">
                      <table class="table table-sm table-bordered">
                        <thead>
                          <tr>
                            <th>Alternative</th>
                            <th>Local Score</th>
                            <th>Weighted Score</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr *ngFor="let score of getAlternativeScoresForCriterion(criterion)">
                            <td>{{ score.alternative.name }}</td>
                            <td>{{ score.localWeight.toFixed(4) }}</td>
                            <td>{{ score.weightedScore.toFixed(4) }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

                <div class="d-flex justify-content-between">
                  <button class="btn btn-outline-secondary" (click)="restartAnalysis()">
                    <i class="bi bi-arrow-counterclockwise"></i> Redo Analysis
                  </button>
                  <div class="d-flex gap-2">
                    <button class="btn btn-primary" (click)="showSaveDialog = true" *ngIf="!showSaveDialog && !savedAnalysisId()">
                      <i class="bi bi-save"></i> Save Analysis
                    </button>
                    <button class="btn btn-success" [routerLink]="['/project', projectName()]">
                      <i class="bi bi-check-circle"></i> Back to Project
                    </button>
                  </div>
                </div>

                <!-- Save Dialog -->
                <div *ngIf="showSaveDialog" class="mt-4 p-3 border rounded bg-light">
                  <h5>Save Analysis</h5>
                  <div class="mb-2">
                    <label class="form-label">Name</label>
                    <input type="text" class="form-control" [(ngModel)]="analysisName" placeholder="e.g., Analysis 2024-01-08">
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Description (optional)</label>
                    <textarea class="form-control" [(ngModel)]="analysisDescription" rows="2" placeholder="Notes about this analysis"></textarea>
                  </div>
                  <div class="d-flex gap-2">
                    <button class="btn btn-success" (click)="saveAnalysis()" [disabled]="!analysisName.trim()">
                      <i class="bi bi-check"></i> Save
                    </button>
                    <button class="btn btn-outline-secondary" (click)="showSaveDialog = false">
                      Cancel
                    </button>
                  </div>
                </div>

                <div *ngIf="savedAnalysisId()" class="alert alert-success mt-3">
                  <i class="bi bi-check-circle-fill"></i> Analysis saved successfully!
                </div>
              </div>
            </div>

            <!-- Pairwise Comparison -->
            <div *ngIf="phase() < 3" class="card mb-4">
              <div class="card-header">
                <h4 class="mb-0"><i class="bi bi-arrows-angle-contract"></i> {{ getComparisonTitle() }}</h4>
              </div>
              <div class="card-body">
                <p class="text-muted">{{ getComparisonDescription() }}</p>

                <div *ngIf="currentComparison()" class="comparison-section">
                  <div class="alert alert-info">
                    <strong>Comparison {{ currentComparisonIndex() + 1 }} of {{ totalComparisons() }}</strong>
                    <span class="ms-2 text-muted">({{ getCurrentPhase() }})</span>
                  </div>

                  <div class="row text-center mb-4">
                    <div class="col-md-5">
                      <div class="card bg-light">
                        <div class="card-body">
                          <h5>{{ currentComparison().item1.name }}</h5>
                          <p class="text-muted small mb-0" *ngIf="currentComparison().item1.beschreibung">
                            {{ currentComparison().item1.beschreibung }}
                          </p>
                        </div>
                      </div>
                    </div>
                    <div class="col-md-2 d-flex align-items-center justify-content-center">
                      <h3 class="text-muted">vs</h3>
                    </div>
                    <div class="col-md-5">
                      <div class="card bg-light">
                        <div class="card-body">
                          <h5>{{ currentComparison().item2.name }}</h5>
                          <p class="text-muted small mb-0" *ngIf="currentComparison().item2.beschreibung">
                            {{ currentComparison().item2.beschreibung }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="text-center mb-4">
                    <label class="form-label"><strong>Which is more important relative to {{ getContextLabel() }}?</strong></label>
                    
                    <div class="d-flex justify-content-between align-items-center mb-3">
                      <span class="text-primary"><strong>← {{ currentComparison().item1.name }}</strong></span>
                      <span class="text-muted">vs</span>
                      <span class="text-success"><strong>{{ currentComparison().item2.name }} →</strong></span>
                    </div>
                    
                    <div class="btn-group d-flex flex-wrap justify-content-center gap-2" role="group">
                      <button 
                        *ngFor="let scale of ahpScale" 
                        type="button" 
                        class="btn"
                        [class.btn-primary]="scale.direction === 'left' && selectedValue() === scale.value"
                        [class.btn-outline-primary]="scale.direction === 'left' && selectedValue() !== scale.value"
                        [class.btn-secondary]="scale.direction === 'equal'"
                        [class.btn-success]="scale.direction === 'right' && selectedValue() === scale.value"
                        [class.btn-outline-success]="scale.direction === 'right' && selectedValue() !== scale.value"
                        (click)="selectValue(scale.value)"
                        style="min-width: 60px;">
                        {{ scale.label }}
                      </button>
                    </div>
                    <small class="form-text text-muted d-block mt-2">
                      {{ getScaleDescription() }}
                    </small>
                  </div>

                  <div class="d-flex justify-content-between">
                    <button 
                      class="btn btn-outline-secondary" 
                      (click)="previousComparison()"
                      [disabled]="currentComparisonIndex() === 0">
                      <i class="bi bi-arrow-left"></i> Previous
                    </button>
                    <button 
                      class="btn btn-primary" 
                      (click)="nextComparison()"
                      [disabled]="selectedValue() === null">
                      {{ isLastComparison() ? 'Calculate Results' : 'Next' }} <i class="bi bi-arrow-right"></i>
                    </button>
                  </div>
                </div>

                <div *ngIf="!currentComparison() && (criteria().length < 2 || alternatives().length < 2)" class="alert alert-warning">
                  You need at least 2 criteria and 2 alternatives to perform AHP analysis.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .comparison-section {
      padding: 20px;
    }
    .btn-group button {
      margin: 5px;
    }
  `]
})
export class AnalysisComponent implements OnInit {
  project = signal<Project | null>(null);
  projectName = signal<string>('');
  isLoading = signal(true);
  goals = signal<Node[]>([]);
  criteria = signal<Node[]>([]);
  alternatives = signal<Node[]>([]);
  
  phase = signal(1); // 1 = Criteria comparison, 2 = Alternatives comparison per criterion, 3 = Results
  currentCriterionIndex = signal(0);
  
  criteriaComparisons = signal<any[]>([]);
  alternativeComparisons = signal<Map<string, any[]>>(new Map());
  
  comparisons = signal<any[]>([]);
  currentComparisonIndex = signal(0);
  currentComparison = signal<any>(null);
  selectedValue = signal<number | null>(null);
  
  totalComparisons = signal(0);
  
  // Results
  criteriaWeights = signal<any[]>([]);
  alternativeScoresPerCriterion = signal<Map<string, any[]>>(new Map());
  finalResults = signal<any[]>([]);

  // Save functionality
  showSaveDialog = false;
  analysisName = '';
  analysisDescription = '';
  savedAnalysisId = signal<number | null>(null);
  isViewMode = signal(false);
  viewingAnalysisId = signal<number | null>(null);

  @ViewChild('radarChart') radarChartRef!: ElementRef<HTMLCanvasElement>;
  private radarChartInstance: Chart | null = null;

  ahpScale = [
    { value: 9, label: '9', description: 'Extreme importance', direction: 'left' },
    { value: 7, label: '7', description: 'Very strong importance', direction: 'left' },
    { value: 5, label: '5', description: 'Strong importance', direction: 'left' },
    { value: 3, label: '3', description: 'Moderate importance', direction: 'left' },
    { value: 1, label: '1', description: 'Equal importance', direction: 'equal' },
    { value: 3, label: '3', description: 'Moderate importance', direction: 'right' },
    { value: 5, label: '5', description: 'Strong importance', direction: 'right' },
    { value: 7, label: '7', description: 'Very strong importance', direction: 'right' },
    { value: 9, label: '9', description: 'Extreme importance', direction: 'right' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private nodeService: NodeService,
    private analysisService: AnalysisService
  ) {
    // Register Chart.js components
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    const projectName = this.route.snapshot.paramMap.get('name');
    const analysisId = this.route.snapshot.paramMap.get('analysisId');
    
    if (projectName) {
      this.projectName.set(projectName);
      this.loadProjectAndNodes(projectName);
      
      if (analysisId) {
        // View existing analysis
        this.isViewMode.set(true);
        this.viewingAnalysisId.set(Number(analysisId));
        this.loadSavedAnalysis(projectName, Number(analysisId));
      }
    }
  }

  loadProjectAndNodes(name: string): void {
    this.isLoading.set(true);
    
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        const project = projects.find(p => p.name === name);
        if (project) {
          this.project.set(project);
          this.loadNodes(name);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        console.error('Error loading project:', error);
        this.isLoading.set(false);
        this.router.navigate(['/']);
      }
    });
  }

  loadNodes(projectName: string): void {
    this.nodeService.getNodes(projectName).subscribe({
      next: (nodes) => {
        const goals = nodes.filter(n => n.content === 'GOAL');
        const criteria = nodes.filter(n => n.content === 'CRITERION');
        const alternatives = nodes.filter(n => n.content === 'ALTERNATIVE');
        
        this.goals.set(goals);
        this.criteria.set(criteria);
        this.alternatives.set(alternatives);
        
        this.initializeComparisons();
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading nodes:', error);
        this.isLoading.set(false);
      }
    });
  }

  initializeComparisons(): void {
    // Phase 1: Compare criteria against goal
    const crits = this.criteria();
    const critPairs: any[] = [];
    
    for (let i = 0; i < crits.length; i++) {
      for (let j = i + 1; j < crits.length; j++) {
        critPairs.push({
          item1: crits[i],
          item2: crits[j],
          value: null,
          type: 'criteria'
        });
      }
    }
    
    this.criteriaComparisons.set(critPairs);
    
    // Phase 2: For each criterion, compare alternatives
    const altCompMap = new Map<string, any[]>();
    const alts = this.alternatives();
    
    crits.forEach(criterion => {
      const altPairs: any[] = [];
      for (let i = 0; i < alts.length; i++) {
        for (let j = i + 1; j < alts.length; j++) {
          altPairs.push({
            item1: alts[i],
            item2: alts[j],
            criterion: criterion,
            value: null,
            type: 'alternatives'
          });
        }
      }
      altCompMap.set(criterion.name, altPairs);
    });
    
    this.alternativeComparisons.set(altCompMap);
    
    // Start with criteria comparisons
    this.loadPhaseComparisons();
  }

  loadPhaseComparisons(): void {
    if (this.phase() === 1) {
      // Load criteria comparisons
      const comps = this.criteriaComparisons();
      this.comparisons.set(comps);
      this.totalComparisons.set(comps.length);
      if (comps.length > 0) {
        this.currentComparison.set(comps[0]);
        this.selectedValue.set(comps[0].value);
      }
    } else if (this.phase() === 2) {
      // Load alternative comparisons for current criterion
      const criterion = this.criteria()[this.currentCriterionIndex()];
      if (criterion) {
        const comps = this.alternativeComparisons().get(criterion.name) || [];
        this.comparisons.set(comps);
        this.totalComparisons.set(comps.length);
        this.currentComparisonIndex.set(0);
        if (comps.length > 0) {
          this.currentComparison.set(comps[0]);
          this.selectedValue.set(comps[0].value);
        }
      }
    }
  }

  getCurrentPhase(): string {
    if (this.phase() === 1) {
      return 'Phase 1: Comparing Criteria';
    } else if (this.phase() === 2) {
      const criterion = this.criteria()[this.currentCriterionIndex()];
      return `Phase 2: Comparing Alternatives for "${criterion?.name}" (${this.currentCriterionIndex() + 1}/${this.criteria().length})`;
    }
    return 'Phase 3: Results';
  }

  getComparisonTitle(): string {
    if (this.phase() === 1) {
      return 'Pairwise Comparison of Criteria';
    } else if (this.phase() === 2) {
      const criterion = this.criteria()[this.currentCriterionIndex()];
      return `Pairwise Comparison of Alternatives (for ${criterion?.name})`;
    }
    return 'Analysis Complete';
  }

  getComparisonDescription(): string {
    if (this.phase() === 1) {
      return `Compare each pair of criteria relative to the goal: ${this.goals()[0]?.name}`;
    } else if (this.phase() === 2) {
      const criterion = this.criteria()[this.currentCriterionIndex()];
      return `Compare each pair of alternatives relative to the criterion: ${criterion?.name}`;
    }
    return '';
  }

  getOverallProgress(): number {
    const totalCritComps = this.criteriaComparisons().length;
    const totalAltComps = Array.from(this.alternativeComparisons().values())
      .reduce((sum, comps) => sum + comps.length, 0);
    const totalAllComps = totalCritComps + totalAltComps;
    
    if (totalAllComps === 0) return 0;
    
    const completedCritComps = this.criteriaComparisons().filter(c => c.value !== null).length;
    const completedAltComps = Array.from(this.alternativeComparisons().values())
      .reduce((sum, comps) => sum + comps.filter((c: any) => c.value !== null).length, 0);
    
    const completed = completedCritComps + completedAltComps;
    return Math.round((completed / totalAllComps) * 100);
  }

  selectValue(value: number): void {
    this.selectedValue.set(value);
  }

  getScaleDescription(): string {
    const selected = this.selectedValue();
    if (selected === null) return 'Select a value to indicate relative importance';
    
    const scale = this.ahpScale.find(s => s.value === selected);
    if (!scale) return '';
    
    const comp = this.currentComparison();
    if (!comp) return scale.description;
    
    if (scale.direction === 'equal') {
      return `${scale.description} between ${comp.item1.name} and ${comp.item2.name}`;
    } else if (scale.direction === 'left') {
      return `${scale.description} of ${comp.item1.name} over ${comp.item2.name}`;
    } else {
      return `${scale.description} of ${comp.item2.name} over ${comp.item1.name}`;
    }
  }

  getContextLabel(): string {
    if (this.phase() === 1) {
      return this.goals()[0]?.name || 'the goal';
    } else if (this.phase() === 2) {
      const criterion = this.criteria()[this.currentCriterionIndex()];
      return criterion?.name || 'the criterion';
    }
    return '';
  }

  nextComparison(): void {
    if (this.selectedValue() === null) return;
    
    // Save current comparison value
    const comps = this.comparisons();
    comps[this.currentComparisonIndex()].value = this.selectedValue();
    
    if (this.isLastComparison()) {
      // Check if we need to move to next phase or criterion
      if (this.phase() === 1) {
        // Move to phase 2 (alternatives)
        this.phase.set(2);
        this.currentCriterionIndex.set(0);
        this.currentComparisonIndex.set(0);
        this.loadPhaseComparisons();
      } else if (this.phase() === 2) {
        // Check if there are more criteria
        if (this.currentCriterionIndex() < this.criteria().length - 1) {
          // Move to next criterion
          this.currentCriterionIndex.update(i => i + 1);
          this.currentComparisonIndex.set(0);
          this.loadPhaseComparisons();
        } else {
          // All done, calculate results
          this.phase.set(3);
          this.calculateResults();
        }
      }
    } else {
      const nextIndex = this.currentComparisonIndex() + 1;
      this.currentComparisonIndex.set(nextIndex);
      this.currentComparison.set(comps[nextIndex]);
      this.selectedValue.set(comps[nextIndex].value);
    }
  }

  previousComparison(): void {
    if (this.currentComparisonIndex() > 0) {
      const prevIndex = this.currentComparisonIndex() - 1;
      this.currentComparisonIndex.set(prevIndex);
      const comps = this.comparisons();
      this.currentComparison.set(comps[prevIndex]);
      this.selectedValue.set(comps[prevIndex].value);
    } else {
      // Go back to previous phase or criterion
      if (this.phase() === 2 && this.currentCriterionIndex() > 0) {
        // Go to previous criterion
        this.currentCriterionIndex.update(i => i - 1);
        this.loadPhaseComparisons();
        const comps = this.comparisons();
        const lastIndex = comps.length - 1;
        this.currentComparisonIndex.set(lastIndex);
        this.currentComparison.set(comps[lastIndex]);
        this.selectedValue.set(comps[lastIndex].value);
      } else if (this.phase() === 2 && this.currentCriterionIndex() === 0) {
        // Go back to phase 1
        this.phase.set(1);
        this.loadPhaseComparisons();
        const comps = this.comparisons();
        const lastIndex = comps.length - 1;
        this.currentComparisonIndex.set(lastIndex);
        this.currentComparison.set(comps[lastIndex]);
        this.selectedValue.set(comps[lastIndex].value);
      }
    }
  }

  isLastComparison(): boolean {
    return this.currentComparisonIndex() === this.totalComparisons() - 1;
  }

  calculateResults(): void {
    // Calculate criteria weights
    const critWeights = this.calculateWeightsFromComparisons(
      this.criteriaComparisons(),
      this.criteria()
    );
    
    // Map to criterion for display
    const criteriaWeights = critWeights.map(w => ({
      criterion: w.item,
      weight: w.weight
    }));
    this.criteriaWeights.set(criteriaWeights);

    // Calculate alternative weights for each criterion
    const altScoresMap = new Map<string, any[]>();
    this.criteria().forEach(criterion => {
      const comps = this.alternativeComparisons().get(criterion.name) || [];
      const weights = this.calculateWeightsFromComparisons(comps, this.alternatives());
      
      const critWeight = criteriaWeights.find(w => w.criterion.id === criterion.id)?.weight || 0;
      const scoresWithWeighted = weights.map(w => ({
        alternative: w.item,
        localWeight: w.weight,
        weightedScore: w.weight * critWeight
      }));
      
      altScoresMap.set(criterion.name, scoresWithWeighted);
    });
    this.alternativeScoresPerCriterion.set(altScoresMap);

    // Calculate final scores for alternatives
    const finalScores = this.alternatives().map(alternative => {
      let totalScore = 0;
      
      this.criteria().forEach(criterion => {
        const scores = altScoresMap.get(criterion.name) || [];
        const altScore = scores.find(s => s.alternative.id === alternative.id);
        if (altScore) {
          totalScore += altScore.weightedScore;
        }
      });
      
      return {
        alternative: alternative,
        score: totalScore
      };
    });

    // Sort by score descending
    finalScores.sort((a, b) => b.score - a.score);
    this.finalResults.set(finalScores);

    // Create radar chart after a short delay to ensure DOM is ready
    setTimeout(() => this.createRadarChart(), 100);
  }

  createRadarChart(): void {
    if (!this.radarChartRef?.nativeElement) {
      console.warn('Radar chart canvas not available yet');
      return;
    }

    // Destroy existing chart if it exists
    if (this.radarChartInstance) {
      this.radarChartInstance.destroy();
    }

    const criteriaWeights = this.criteriaWeights();
    const alternatives = this.alternatives();
    const criteria = this.criteria();
    
    if (criteriaWeights.length === 0 || alternatives.length === 0) return;

    // Labels are the criteria names
    const labels = criteria.map(c => c.name);

    // Generate a color palette for alternatives
    const colors = [
      { bg: 'rgba(255, 99, 132, 0.2)', border: 'rgb(255, 99, 132)' },    // Red
      { bg: 'rgba(54, 162, 235, 0.2)', border: 'rgb(54, 162, 235)' },    // Blue
      { bg: 'rgba(75, 192, 192, 0.2)', border: 'rgb(75, 192, 192)' },    // Green
      { bg: 'rgba(255, 206, 86, 0.2)', border: 'rgb(255, 206, 86)' },    // Yellow
      { bg: 'rgba(153, 102, 255, 0.2)', border: 'rgb(153, 102, 255)' },  // Purple
      { bg: 'rgba(255, 159, 64, 0.2)', border: 'rgb(255, 159, 64)' },    // Orange
      { bg: 'rgba(201, 203, 207, 0.2)', border: 'rgb(201, 203, 207)' },  // Grey
      { bg: 'rgba(255, 99, 255, 0.2)', border: 'rgb(255, 99, 255)' }     // Pink
    ];

    // Create a dataset for each alternative
    const datasets = alternatives.map((alternative, index) => {
      // For each criterion, get the score of this alternative
      const dataPoints = criteria.map(criterion => {
        const scores = this.alternativeScoresPerCriterion().get(criterion.name) || [];
        const altScore = scores.find(s => s.alternative.id === alternative.id);
        // Use local weight (normalized score for this criterion) and convert to percentage
        return altScore ? (altScore.localWeight * 100) : 0;
      });

      const color = colors[index % colors.length];
      
      return {
        label: alternative.name,
        data: dataPoints,
        backgroundColor: color.bg,
        borderColor: color.border,
        borderWidth: 2,
        pointBackgroundColor: color.border,
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: color.border,
        pointRadius: 4,
        pointHoverRadius: 6
      };
    });

    const config: ChartConfiguration<'radar'> = {
      type: 'radar',
      data: {
        labels: labels,
        datasets: datasets
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        scales: {
          r: {
            beginAtZero: true,
            max: 100,
            ticks: {
              stepSize: 20,
              callback: function(value) {
                return value + '%';
              }
            },
            pointLabels: {
              font: {
                size: 13,
                weight: 'bold'
              }
            },
            grid: {
              color: 'rgba(0, 0, 0, 0.1)'
            }
          }
        },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: {
              font: {
                size: 13
              },
              padding: 15,
              usePointStyle: true
            }
          },
          title: {
            display: true,
            text: `${this.goals()[0]?.name || 'Goal'}: Alternatives Comparison`,
            font: {
              size: 18,
              weight: 'bold'
            },
            padding: 20
          },
          tooltip: {
            callbacks: {
              label: function(context) {
                return context.dataset.label + ': ' + context.parsed.r.toFixed(1) + '%';
              }
            }
          }
        }
      }
    };

    const ctx = this.radarChartRef.nativeElement.getContext('2d');
    if (ctx) {
      this.radarChartInstance = new Chart(ctx, config);
    }
  }

  calculateWeightsFromComparisons(comparisons: any[], items: Node[]): any[] {
    const n = items.length;
    
    // Build pairwise comparison matrix
    const matrix: number[][] = Array(n).fill(0).map(() => Array(n).fill(1));
    
    comparisons.forEach(comp => {
      const i = items.findIndex(item => item.id === comp.item1.id);
      const j = items.findIndex(item => item.id === comp.item2.id);
      
      if (i >= 0 && j >= 0 && comp.value !== null) {
        matrix[i][j] = comp.value;
        matrix[j][i] = 1 / comp.value;
      }
    });

    // Calculate priority vector using geometric mean method
    const weights: number[] = [];
    for (let i = 0; i < n; i++) {
      let product = 1;
      for (let j = 0; j < n; j++) {
        product *= matrix[i][j];
      }
      weights[i] = Math.pow(product, 1 / n);
    }

    // Normalize weights
    const sum = weights.reduce((a, b) => a + b, 0);
    const normalizedWeights = weights.map(w => w / sum);

    // Return results with items
    return items.map((item, i) => ({
      item: item,
      weight: normalizedWeights[i]
    })).sort((a, b) => b.weight - a.weight);
  }

  getAlternativeScoresForCriterion(criterion: Node): any[] {
    return this.alternativeScoresPerCriterion().get(criterion.name) || [];
  }

  restartAnalysis(): void {
    this.phase.set(1);
    this.currentCriterionIndex.set(0);
    this.currentComparisonIndex.set(0);
    this.selectedValue.set(null);
    
    // Reset all comparison values
    this.criteriaComparisons().forEach(c => c.value = null);
    this.alternativeComparisons().forEach(comps => comps.forEach(c => c.value = null));
    
    this.loadPhaseComparisons();
  }

  saveAnalysis(): void {
    if (!this.analysisName.trim()) return;

    try {
      // Convert Map to Object for serialization
      const alternativeComparisonsObj: Record<string, any> = {};
      this.alternativeComparisons().forEach((value, key) => {
        alternativeComparisonsObj[key] = value;
      });

      const alternativeScoresObj: Record<string, any> = {};
      this.alternativeScoresPerCriterion().forEach((value, key) => {
        alternativeScoresObj[key] = value;
      });

      const analysis: Analysis = {
        name: this.analysisName,
        beschreibung: this.analysisDescription,
        criteriaComparisons: JSON.stringify(this.criteriaComparisons()),
        alternativeComparisons: JSON.stringify(alternativeComparisonsObj),
        results: JSON.stringify({
          criteriaWeights: this.criteriaWeights(),
          alternativeScoresPerCriterion: alternativeScoresObj,
          finalResults: this.finalResults()
        })
      };

      console.log('Saving analysis:', analysis);

      this.analysisService.createAnalysis(this.projectName(), analysis).subscribe({
        next: (saved) => {
          console.log('Analysis saved successfully:', saved);
          this.savedAnalysisId.set(saved.id || null);
          this.showSaveDialog = false;
          this.analysisName = '';
          this.analysisDescription = '';
        },
        error: (error) => {
          console.error('Error saving analysis:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          console.error('Error details:', error.error);
          alert(`Failed to save analysis: ${error.status || 'Unknown error'}\n${error.error?.message || error.message || 'Check console for details'}`);
        }
      });
    } catch (err) {
      console.error('Error preparing analysis data:', err);
      alert('Failed to prepare analysis data for saving');
    }
  }

  loadSavedAnalysis(projectName: string, analysisId: number): void {
    this.analysisService.getAnalysis(projectName, analysisId).subscribe({
      next: (analysis) => {
        // Load saved comparisons
        if (analysis.criteriaComparisons) {
          this.criteriaComparisons.set(JSON.parse(analysis.criteriaComparisons));
        }
        if (analysis.alternativeComparisons) {
          const comparisonsObj = JSON.parse(analysis.alternativeComparisons);
          // Convert object back to Map
          const comparisonsMap = new Map<string, any>();
          Object.keys(comparisonsObj).forEach(key => {
            comparisonsMap.set(key, comparisonsObj[key]);
          });
          this.alternativeComparisons.set(comparisonsMap);
        }
        
        // Load saved results
        if (analysis.results) {
          const results = JSON.parse(analysis.results);
          this.criteriaWeights.set(results.criteriaWeights);
          
          // Convert object back to Map
          const scoresMap = new Map<string, any>();
          Object.keys(results.alternativeScoresPerCriterion).forEach(key => {
            scoresMap.set(key, results.alternativeScoresPerCriterion[key]);
          });
          this.alternativeScoresPerCriterion.set(scoresMap);
          
          this.finalResults.set(results.finalResults);
        }
        
        // Go to results phase
        this.phase.set(3);
        this.savedAnalysisId.set(analysisId);

        // Create radar chart after a short delay to ensure DOM is ready
        setTimeout(() => this.createRadarChart(), 200);
      },
      error: (error) => {
        console.error('Error loading analysis:', error);
        alert('Failed to load analysis');
        this.router.navigate(['/project', projectName]);
      }
    });
  }
}
