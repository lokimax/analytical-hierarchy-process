import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Analysis {
  id?: number;
  name: string;
  beschreibung?: string;
  criteriaComparisons?: string;
  alternativeComparisons?: string;
  results?: string;
  completedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {
  private baseUrl = 'http://localhost:9000/api/clients';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getApiUrl(projectName: string): string {
    const user = this.authService.getCurrentUser()();
    return `${this.baseUrl}/${user?.nickname}/projects/${projectName}/analyses`;
  }

  getAnalyses(projectName: string): Observable<Analysis[]> {
    return this.http.get<Analysis[]>(this.getApiUrl(projectName));
  }

  getAnalysis(projectName: string, analysisId: number): Observable<Analysis> {
    return this.http.get<Analysis>(`${this.getApiUrl(projectName)}/${analysisId}`);
  }

  createAnalysis(projectName: string, analysis: Analysis): Observable<Analysis> {
    return this.http.post<Analysis>(this.getApiUrl(projectName), analysis);
  }

  deleteAnalysis(projectName: string, analysisId: number): Observable<void> {
    return this.http.delete<void>(`${this.getApiUrl(projectName)}/${analysisId}`);
  }
}
