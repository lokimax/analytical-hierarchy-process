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
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.css']
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

  private readonly consistencyThreshold = 0.1;
  
  // Results
  criteriaWeights = signal<any[]>([]);
  alternativeScoresPerCriterion = signal<Map<string, any[]>>(new Map());
  finalResults = signal<any[]>([]);
  criteriaConsistency = signal<{ ci: number; cr: number; n: number }>({ ci: 0, cr: 0, n: 0 });
  alternativeConsistency = signal<Map<string, { ci: number; cr: number; n: number }>>(new Map());

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
    { value: 1 / 3, label: '3', description: 'Moderate importance', direction: 'right' },
    { value: 1 / 5, label: '5', description: 'Strong importance', direction: 'right' },
    { value: 1 / 7, label: '7', description: 'Very strong importance', direction: 'right' },
    { value: 1 / 9, label: '9', description: 'Extreme importance', direction: 'right' }
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
    const criteriaResult = this.calculateWeightsAndConsistency(
      this.criteriaComparisons(),
      this.criteria()
    );
    this.criteriaConsistency.set({
      ci: criteriaResult.ci,
      cr: criteriaResult.cr,
      n: criteriaResult.n
    });
    
    // Map to criterion for display
    const criteriaWeights = criteriaResult.weights.map(w => ({
      criterion: w.item,
      weight: w.weight
    }));
    this.criteriaWeights.set(criteriaWeights);

    // Calculate alternative weights for each criterion
    const altScoresMap = new Map<string, any[]>();
    const altConsistencyMap = new Map<string, { ci: number; cr: number; n: number }>();
    this.criteria().forEach(criterion => {
      const comps = this.alternativeComparisons().get(criterion.name) || [];
      const altResult = this.calculateWeightsAndConsistency(comps, this.alternatives());
      altConsistencyMap.set(criterion.name, {
        ci: altResult.ci,
        cr: altResult.cr,
        n: altResult.n
      });
      const weights = altResult.weights;
      
      const critWeight = criteriaWeights.find(w => w.criterion.id === criterion.id)?.weight || 0;
      const scoresWithWeighted = weights.map(w => ({
        alternative: w.item,
        localWeight: w.weight,
        weightedScore: w.weight * critWeight
      }));
      
      altScoresMap.set(criterion.name, scoresWithWeighted);
    });
    this.alternativeScoresPerCriterion.set(altScoresMap);
    this.alternativeConsistency.set(altConsistencyMap);

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

  calculateWeightsAndConsistency(comparisons: any[], items: Node[]): { weights: any[]; ci: number; cr: number; n: number } {
    const n = items.length;

    if (n === 0) {
      return { weights: [], ci: 0, cr: 0, n };
    }

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
    const sum = weights.reduce((a, b) => a + b, 0) || 1;
    const normalizedWeights = weights.map(w => w / sum);

    // Consistency calculation (Saaty CI/CR)
    let lambdaMax = 0;
    for (let i = 0; i < n; i++) {
      let rowSum = 0;
      for (let j = 0; j < n; j++) {
        rowSum += matrix[i][j] * normalizedWeights[j];
      }
      lambdaMax += rowSum / (normalizedWeights[i] || 1);
    }
    lambdaMax = n > 0 ? lambdaMax / n : 0;

    const ci = n > 1 ? (lambdaMax - n) / (n - 1) : 0;
    const randomIndex: Record<number, number> = {
      1: 0,
      2: 0,
      3: 0.58,
      4: 0.90,
      5: 1.12,
      6: 1.24,
      7: 1.32,
      8: 1.41,
      9: 1.45,
      10: 1.49
    };
    const ri = randomIndex[n] || 0;
    const cr = ri > 0 ? ci / ri : 0;

    const weightsWithItems = items.map((item, i) => ({
      item: item,
      weight: normalizedWeights[i]
    })).sort((a, b) => b.weight - a.weight);

    return { weights: weightsWithItems, ci, cr, n };
  }

  getAlternativeScoresForCriterion(criterion: Node): any[] {
    return this.alternativeScoresPerCriterion().get(criterion.name) || [];
  }

  getCriterionConsistency(criterion: Node): { ci: number; cr: number; n: number } | undefined {
    return this.alternativeConsistency().get(criterion.name);
  }

  getConsistencyIconClass(cr?: number | null): string {
    if (cr === null || cr === undefined) return 'bi-question-circle text-muted';
    if (cr < 0.1) return 'bi-check-circle-fill text-success';
    if (cr < 0.2) return 'bi-exclamation-triangle-fill text-warning';
    return 'bi-x-circle-fill text-danger';
  }

  getConsistencyAlertClass(cr?: number | null): string {
    if (cr === null || cr === undefined) return 'alert-secondary';
    if (cr < 0.1) return 'alert-success';
    if (cr < 0.2) return 'alert-warning';
    return 'alert-danger';
  }

  getConsistencyBadgeClass(cr?: number | null): string {
    if (cr === null || cr === undefined) return 'bg-secondary';
    if (cr < 0.1) return 'bg-success';
    if (cr < 0.2) return 'bg-warning text-dark';
    return 'bg-danger';
  }

  getConsistencyStatusText(cr?: number | null): string {
    if (cr === null || cr === undefined) return 'Nicht berechnet';
    if (cr < 0.1) return 'Excellent - Konsistenz akzeptabel';
    if (cr < 0.2) return 'Vorsicht - Inkonsistenz erkannt';
    return 'Kritisch - Starke Inkonsistenz';
  }

  getConsistencyExplanation(cr?: number | null): string {
    if (cr === null || cr === undefined) return '';
    if (cr < 0.1) {
      return 'Die paarweisen Vergleiche sind konsistent. Die Gewichtungen sind zuverlässig.';
    }
    if (cr < 0.2) {
      return 'Die Konsistenz liegt außerhalb des empfohlenen Bereichs (CR > 0.1). Überprüfen Sie Ihre Vergleiche auf Widersprüche. Beispiel: Wenn A > B und B > C, sollte auch A > C gelten.';
    }
    return 'Die Inkonsistenz ist sehr hoch (CR ≥ 0.2). Die Ergebnisse könnten unzuverlässig sein. Bitte überarbeiten Sie Ihre paarweisen Vergleiche sorgfältig.';
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

      const alternativeConsistencyObj: Record<string, any> = {};
      this.alternativeConsistency().forEach((value, key) => {
        alternativeConsistencyObj[key] = value;
      });

      const analysis: Analysis = {
        name: this.analysisName,
        beschreibung: this.analysisDescription,
        criteriaComparisons: JSON.stringify(this.criteriaComparisons()),
        alternativeComparisons: JSON.stringify(alternativeComparisonsObj),
        results: JSON.stringify({
          criteriaWeights: this.criteriaWeights(),
          alternativeScoresPerCriterion: alternativeScoresObj,
          finalResults: this.finalResults(),
          criteriaConsistency: this.criteriaConsistency(),
          alternativeConsistency: alternativeConsistencyObj
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
          if (results.criteriaConsistency) {
            this.criteriaConsistency.set(results.criteriaConsistency);
          }
          
          // Convert object back to Map
          const scoresMap = new Map<string, any>();
          Object.keys(results.alternativeScoresPerCriterion).forEach(key => {
            scoresMap.set(key, results.alternativeScoresPerCriterion[key]);
          });
          this.alternativeScoresPerCriterion.set(scoresMap);
          
          if (results.alternativeConsistency) {
            const altConsMap = new Map<string, { ci: number; cr: number; n: number }>();
            Object.keys(results.alternativeConsistency).forEach(key => {
              altConsMap.set(key, results.alternativeConsistency[key]);
            });
            this.alternativeConsistency.set(altConsMap);
          }
          
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
