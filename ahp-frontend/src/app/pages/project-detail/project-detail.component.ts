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
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
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
