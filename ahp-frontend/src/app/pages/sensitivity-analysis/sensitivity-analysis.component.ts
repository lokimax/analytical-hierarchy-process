import { Component, OnInit, OnDestroy, signal, computed } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnalysisService } from '../../services/analysis.service';
import { SensitivityResult, RiskLevel } from '../../models/sensitivity.model';
import { Chart, ChartConfiguration } from 'chart.js/auto';

@Component({
  selector: 'app-sensitivity-analysis',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sensitivity-analysis.component.html',
  styleUrl: './sensitivity-analysis.component.css'
})
export class SensitivityAnalysisComponent implements OnInit, OnDestroy {
  projectName = signal<string>('');
  analysisId = signal<number>(0);
  criterionId = signal<number>(0);
  
  result = signal<SensitivityResult | null>(null);
  loading = signal<boolean>(false);
  error = signal<string>('');
  
  private chart: Chart | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private analysisService: AnalysisService
  ) {}

  ngOnInit(): void {
    this.projectName.set(this.route.snapshot.paramMap.get('projectName') || '');
    const analysisIdParam = this.route.snapshot.paramMap.get('analysisId');
    const criterionIdParam = this.route.snapshot.paramMap.get('criterionId');

    if (analysisIdParam && criterionIdParam) {
      this.analysisId.set(parseInt(analysisIdParam));
      this.criterionId.set(parseInt(criterionIdParam));
      this.loadSensitivityAnalysis();
    } else {
      this.error.set('Missing analysis or criterion ID');
    }
  }

  ngOnDestroy(): void {
    if (this.chart) {
      this.chart.destroy();
    }
  }

  loadSensitivityAnalysis(): void {
    this.loading.set(true);
    this.error.set('');

    this.analysisService
      .getSensitivityAnalysis(this.projectName(), this.analysisId(), this.criterionId())
      .subscribe({
        next: (data) => {
          this.result.set(data);
          this.loading.set(false);
          setTimeout(() => this.createChart(), 100);
        },
        error: (err) => {
          this.error.set(err.error?.message || 'Failed to load sensitivity analysis');
          this.loading.set(false);
        }
      });
  }

  createChart(): void {
    const canvas = document.getElementById('sensitivityChart') as HTMLCanvasElement;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const data = this.result();
    if (!data) return;

    const labels = data.dataPoints.map(p => `${(p.criterionWeight * 100).toFixed(0)}%`);
    
    const datasets = data.alternativeNames.map((name, index) => {
      const alternativeId = index + 1; // Assuming IDs start at 1
      const scores = data.dataPoints.map(p => p.alternativeScores[alternativeId] || 0);
      
      return {
        label: name,
        data: scores,
        borderColor: this.getColorForIndex(index),
        backgroundColor: this.getColorForIndex(index, 0.1),
        borderWidth: 2,
        tension: 0.4,
        pointRadius: 0,
        pointHoverRadius: 5
      };
    });

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: labels,
        datasets: datasets
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: `Sensitivity Analysis: ${data.criterionName}`,
            font: { size: 16 }
          },
          legend: {
            display: true,
            position: 'top'
          },
          tooltip: {
            mode: 'index',
            intersect: false,
            callbacks: {
              label: (context) => {
                const value = context.parsed.y;
                return `${context.dataset.label}: ${value !== null ? value.toFixed(3) : 'N/A'}`;
              }
            }
          }
        },
        scales: {
          x: {
            display: true,
            title: {
              display: true,
              text: 'Criterion Weight'
            }
          },
          y: {
            display: true,
            title: {
              display: true,
              text: 'Alternative Score'
            },
            beginAtZero: true
          }
        },
        interaction: {
          mode: 'nearest',
          axis: 'x',
          intersect: false
        }
      }
    };

    // Note: Critical point annotations removed due to Chart.js plugin requirements
    // Can be added with chartjs-plugin-annotation if needed

    this.chart = new Chart(ctx, config);
  }

  getColorForIndex(index: number, alpha: number = 1): string {
    const colors = [
      `rgba(54, 162, 235, ${alpha})`,   // Blue
      `rgba(255, 99, 132, ${alpha})`,   // Red
      `rgba(75, 192, 192, ${alpha})`,   // Green
      `rgba(255, 159, 64, ${alpha})`,   // Orange
      `rgba(153, 102, 255, ${alpha})`,  // Purple
      `rgba(255, 205, 86, ${alpha})`    // Yellow
    ];
    return colors[index % colors.length];
  }

  getRiskLevelClass(level: RiskLevel): string {
    switch (level) {
      case RiskLevel.LOW:
        return 'success';
      case RiskLevel.MEDIUM:
        return 'warning';
      case RiskLevel.HIGH:
        return 'danger';
      default:
        return 'secondary';
    }
  }

  getRiskIcon(level: RiskLevel): string {
    switch (level) {
      case RiskLevel.LOW:
        return '✅';
      case RiskLevel.MEDIUM:
        return '⚠️';
      case RiskLevel.HIGH:
        return '🔴';
      default:
        return '❓';
    }
  }

  goBack(): void {
    this.router.navigate(['/projects', this.projectName(), 'analyses', this.analysisId()]);
  }
}
