import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProjectService, Project } from '../../services/project.service';
import { NodeService, Node } from '../../services/node.service';
import { AnalysisService, Analysis } from '../../services/analysis.service';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container mt-4">
      <div class="row">
        <div class="col-md-10 offset-md-1">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
              <a routerLink="/" class="btn btn-outline-secondary mb-2">
                <i class="bi bi-arrow-left"></i> Back to Projects
              </a>
              <h1 class="mb-1">{{ project()?.name }}</h1>
              <p class="text-muted" *ngIf="project()?.beschreibung">{{ project()?.beschreibung }}</p>
            </div>
          </div>

          <div *ngIf="isLoading()" class="text-center py-5">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>

          <div *ngIf="!isLoading() && project()">
            <!-- Ziel Section -->
            <div class="card mb-4">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h4 class="mb-0">Ziel (Goal)</h4>
                <button class="btn btn-sm btn-primary" (click)="showZielForm = !showZielForm">
                  <i class="bi bi-plus-circle"></i> {{ showZielForm ? 'Cancel' : 'Add Goal' }}
                </button>
              </div>
              <div class="card-body">
                <div *ngIf="showZielForm" class="mb-3">
                  <div class="mb-2">
                    <input 
                      type="text" 
                      class="form-control" 
                      [(ngModel)]="newZiel.name"
                      placeholder="Goal name"
                    >
                  </div>
                  <div class="mb-2">
                    <textarea 
                      class="form-control" 
                      [(ngModel)]="newZiel.beschreibung"
                      placeholder="Goal description"
                      rows="2"
                    ></textarea>
                  </div>
                  <button class="btn btn-success btn-sm" (click)="addZiel()">Save Goal</button>
                </div>

                <div *ngIf="ziele().length === 0 && !showZielForm" class="text-muted text-center py-3">
                  No goals defined yet. Click "Add Goal" to create one.
                </div>

                <div *ngIf="ziele().length > 0" class="list-group">
                  <div *ngFor="let ziel of ziele()" class="list-group-item">
                    <div class="d-flex justify-content-between align-items-start">
                      <div class="flex-grow-1">
                        <h5 class="mb-1">{{ ziel.name }}</h5>
                        <p class="mb-0 text-muted" *ngIf="ziel.beschreibung">{{ ziel.beschreibung }}</p>
                      </div>
                      <button class="btn btn-sm btn-outline-danger" (click)="removeZiel(ziel)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Kriterien Section -->
            <div class="card mb-4">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h4 class="mb-0">Kriterien (Criteria)</h4>
                <button class="btn btn-sm btn-primary" (click)="showKriteriumForm = !showKriteriumForm">
                  <i class="bi bi-plus-circle"></i> {{ showKriteriumForm ? 'Cancel' : 'Add Criterion' }}
                </button>
              </div>
              <div class="card-body">
                <div *ngIf="showKriteriumForm" class="mb-3">
                  <div class="mb-2">
                    <input 
                      type="text" 
                      class="form-control" 
                      [(ngModel)]="newKriterium.name"
                      placeholder="Criterion name"
                    >
                  </div>
                  <div class="mb-2">
                    <textarea 
                      class="form-control" 
                      [(ngModel)]="newKriterium.beschreibung"
                      placeholder="Criterion description"
                      rows="2"
                    ></textarea>
                  </div>
                  <button class="btn btn-success btn-sm" (click)="addKriterium()">Save Criterion</button>
                </div>

                <div *ngIf="kriterien().length === 0 && !showKriteriumForm" class="text-muted text-center py-3">
                  No criteria defined yet. Click "Add Criterion" to create one.
                </div>

                <div *ngIf="kriterien().length > 0" class="list-group">
                  <div *ngFor="let kriterium of kriterien()" class="list-group-item">
                    <div class="d-flex justify-content-between align-items-start">
                      <div class="flex-grow-1">
                        <h5 class="mb-1">{{ kriterium.name }}</h5>
                        <p class="mb-0 text-muted" *ngIf="kriterium.beschreibung">{{ kriterium.beschreibung }}</p>
                      </div>
                      <button class="btn btn-sm btn-outline-danger" (click)="removeKriterium(kriterium)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Alternativen Section -->
            <div class="card mb-4">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h4 class="mb-0">Alternativen (Alternatives)</h4>
                <button class="btn btn-sm btn-primary" (click)="showAlternativeForm = !showAlternativeForm">
                  <i class="bi bi-plus-circle"></i> {{ showAlternativeForm ? 'Cancel' : 'Add Alternative' }}
                </button>
              </div>
              <div class="card-body">
                <div *ngIf="showAlternativeForm" class="mb-3">
                  <div class="mb-2">
                    <input 
                      type="text" 
                      class="form-control" 
                      [(ngModel)]="newAlternative.name"
                      placeholder="Alternative name"
                    >
                  </div>
                  <div class="mb-2">
                    <textarea 
                      class="form-control" 
                      [(ngModel)]="newAlternative.beschreibung"
                      placeholder="Alternative description"
                      rows="2"
                    ></textarea>
                  </div>
                  <button class="btn btn-success btn-sm" (click)="addAlternative()">Save Alternative</button>
                </div>

                <div *ngIf="alternativen().length === 0 && !showAlternativeForm" class="text-muted text-center py-3">
                  No alternatives defined yet. Click "Add Alternative" to create one.
                </div>

                <div *ngIf="alternativen().length > 0" class="list-group">
                  <div *ngFor="let alternative of alternativen()" class="list-group-item">
                    <div class="d-flex justify-content-between align-items-start">
                      <div class="flex-grow-1">
                        <h5 class="mb-1">{{ alternative.name }}</h5>
                        <p class="mb-0 text-muted" *ngIf="alternative.beschreibung">{{ alternative.beschreibung }}</p>
                      </div>
                      <button class="btn btn-sm btn-outline-danger" (click)="removeAlternative(alternative)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="d-flex justify-content-end gap-2 mb-4">
              <button class="btn btn-outline-secondary" routerLink="/">Cancel</button>
              <button 
                class="btn btn-primary" 
                [disabled]="ziele().length === 0 || kriterien().length === 0 || alternativen().length === 0"
                (click)="continueToAnalysis()">
                <i class="bi bi-calculator"></i> Start New Analysis
              </button>
            </div>

            <!-- Previous Analyses -->
            <div class="card mb-4">
              <div class="card-header">
                <h4 class="mb-0"><i class="bi bi-journal-text"></i> Previous Analyses</h4>
              </div>
              <div class="card-body">
                <div *ngIf="isLoadingAnalyses()" class="text-center py-3">
                  <div class="spinner-border spinner-border-sm" role="status">
                    <span class="visually-hidden">Loading...</span>
                  </div>
                </div>

                <div *ngIf="!isLoadingAnalyses() && analyses().length === 0" class="text-muted text-center py-3">
                  No analyses yet. Start your first analysis!
                </div>

                <div *ngIf="!isLoadingAnalyses() && analyses().length > 0" class="list-group">
                  <div *ngFor="let analysis of analyses()" class="list-group-item">
                    <div class="d-flex justify-content-between align-items-start">
                      <div class="flex-grow-1">
                        <h5 class="mb-1">{{ analysis.name }}</h5>
                        <div *ngIf="getCriteriaConsistencySummary(analysis) as cons" class="d-flex align-items-center gap-2 mb-1">
                          <span class="badge" [ngClass]="getConsistencyBadgeClass(cons.cr)">CR {{ cons.cr | number:'1.3-3' }}</span>
                          <span class="text-muted small">CI {{ cons.ci | number:'1.3-3' }}</span>
                        </div>
                        <p class="mb-1 text-muted small" *ngIf="analysis.beschreibung">{{ analysis.beschreibung }}</p>
                        <small class="text-muted">
                          Completed: {{ formatDate(analysis.completedAt) }}
                        </small>
                      </div>
                      <div class="btn-group">
                        <button class="btn btn-sm btn-outline-primary" (click)="viewAnalysis(analysis)">
                          <i class="bi bi-eye"></i> View
                        </button>
                        <button class="btn btn-sm btn-outline-danger" (click)="deleteAnalysis(analysis)">
                          <i class="bi bi-trash"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .list-group-item:hover {
      background-color: #f8f9fa;
    }
  `]
})
export class ProjectDetailComponent implements OnInit {
  project = signal<Project | null>(null);
  isLoading = signal(true);
  ziele = signal<Node[]>([]);
  kriterien = signal<Node[]>([]);
  alternativen = signal<Node[]>([]);
  analyses = signal<Analysis[]>([]);
  isLoadingAnalyses = signal(false);
  private readonly consistencyThreshold = 0.1;
  
  showZielForm = false;
  showKriteriumForm = false;
  showAlternativeForm = false;
  
  newZiel = { name: '', beschreibung: '' };
  newKriterium = { name: '', beschreibung: '' };
  newAlternative = { name: '', beschreibung: '' };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private nodeService: NodeService,
    private analysisService: AnalysisService
  ) {}

  ngOnInit(): void {
    const projectName = this.route.snapshot.paramMap.get('name');
    if (projectName) {
      this.loadProject(projectName);
      this.loadNodes(projectName);
      this.loadAnalyses(projectName);
    }
  }

  loadProject(name: string): void {
    this.isLoading.set(true);
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        const project = projects.find(p => p.name === name);
        if (project) {
          this.project.set(project);
        } else {
          this.router.navigate(['/']);
        }
        this.isLoading.set(false);
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
        this.ziele.set(goals);
        this.kriterien.set(criteria);
        this.alternativen.set(alternatives);
      },
      error: (error) => {
        console.error('Error loading nodes:', error);
      }
    });
  }

  addZiel(): void {
    if (this.newZiel.name.trim() && this.project()) {
      const node: Node = {
        name: this.newZiel.name,
        beschreibung: this.newZiel.beschreibung,
        content: 'GOAL'
      };

      this.nodeService.createNode(this.project()!.name, node).subscribe({
        next: (createdNode) => {
          this.ziele.update(z => [...z, createdNode]);
          this.newZiel = { name: '', beschreibung: '' };
          this.showZielForm = false;
        },
        error: (error) => {
          console.error('Error creating goal:', error);
          alert('Fehler beim Erstellen des Ziels');
        }
      });
    }
  }

  removeZiel(ziel: Node): void {
    if (this.project() && ziel.name) {
      this.nodeService.deleteNode(this.project()!.name, ziel.name).subscribe({
        next: () => {
          this.ziele.update(z => z.filter(item => item.id !== ziel.id));
        },
        error: (error) => {
          console.error('Error deleting goal:', error);
          alert('Fehler beim Löschen des Ziels');
        }
      });
    }
  }

  addKriterium(): void {
    if (this.newKriterium.name.trim() && this.project()) {
      const node: Node = {
        name: this.newKriterium.name,
        beschreibung: this.newKriterium.beschreibung,
        content: 'CRITERION'
      };

      this.nodeService.createNode(this.project()!.name, node).subscribe({
        next: (createdNode) => {
          this.kriterien.update(k => [...k, createdNode]);
          this.newKriterium = { name: '', beschreibung: '' };
          this.showKriteriumForm = false;
        },
        error: (error) => {
          console.error('Error creating criterion:', error);
          alert('Fehler beim Erstellen des Kriteriums');
        }
      });
    }
  }

  removeKriterium(kriterium: Node): void {
    if (this.project() && kriterium.name) {
      this.nodeService.deleteNode(this.project()!.name, kriterium.name).subscribe({
        next: () => {
          this.kriterien.update(k => k.filter(item => item.id !== kriterium.id));
        },
        error: (error) => {
          console.error('Error deleting criterion:', error);
          alert('Fehler beim Löschen des Kriteriums');
        }
      });
    }
  }

  addAlternative(): void {
    if (this.newAlternative.name.trim() && this.project()) {
      const node: Node = {
        name: this.newAlternative.name,
        beschreibung: this.newAlternative.beschreibung,
        content: 'ALTERNATIVE'
      };

      this.nodeService.createNode(this.project()!.name, node).subscribe({
        next: (createdNode) => {
          this.alternativen.update(a => [...a, createdNode]);
          this.newAlternative = { name: '', beschreibung: '' };
          this.showAlternativeForm = false;
        },
        error: (error) => {
          console.error('Error creating alternative:', error);
          alert('Fehler beim Erstellen der Alternative');
        }
      });
    }
  }

  removeAlternative(alternative: Node): void {
    if (this.project() && alternative.name) {
      this.nodeService.deleteNode(this.project()!.name, alternative.name).subscribe({
        next: () => {
          this.alternativen.update(a => a.filter(item => item.id !== alternative.id));
        },
        error: (error) => {
          console.error('Error deleting alternative:', error);
          alert('Fehler beim Löschen der Alternative');
        }
      });
    }
  }

  continueToAnalysis(): void {
    if (this.project()) {
      this.router.navigate(['/analysis', this.project()!.name]);
    }
  }

  loadAnalyses(projectName: string): void {
    this.isLoadingAnalyses.set(true);
    this.analysisService.getAnalyses(projectName).subscribe({
      next: (analyses) => {
        this.analyses.set(analyses);
        this.isLoadingAnalyses.set(false);
      },
      error: (error) => {
        console.error('Error loading analyses:', error);
        this.isLoadingAnalyses.set(false);
      }
    });
  }

  viewAnalysis(analysis: Analysis): void {
    if (this.project() && analysis.id) {
      this.router.navigate(['/analysis', this.project()!.name, analysis.id]);
    }
  }

  deleteAnalysis(analysis: Analysis): void {
    if (!confirm(`Delete analysis "${analysis.name}"?`)) return;
    
    if (this.project() && analysis.id) {
      this.analysisService.deleteAnalysis(this.project()!.name, analysis.id).subscribe({
        next: () => {
          this.analyses.update(list => list.filter(a => a.id !== analysis.id));
        },
        error: (error) => {
          console.error('Error deleting analysis:', error);
          alert('Failed to delete analysis');
        }
      });
    }
  }

  getCriteriaConsistencySummary(analysis: Analysis): { ci: number; cr: number } | null {
    if (!analysis.results) return null;
    try {
      const parsed = JSON.parse(analysis.results);
      if (parsed?.criteriaConsistency && typeof parsed.criteriaConsistency.cr === 'number') {
        return parsed.criteriaConsistency;
      }
    } catch (err) {
      console.warn('Could not parse analysis results for consistency', err);
    }
    return null;
  }

  getConsistencyBadgeClass(cr?: number | null): string {
    if (cr === null || cr === undefined) return 'bg-secondary';
    return cr <= this.consistencyThreshold ? 'bg-success' : 'bg-warning text-dark';
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString('de-DE', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
