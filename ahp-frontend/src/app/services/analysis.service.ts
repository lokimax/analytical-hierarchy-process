import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SensitivityResult } from '../models/sensitivity.model';

export interface Analysis {
  id?: number;
  name: string;
  beschreibung?: string;
  projectId?: number;
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
  private apiUrl = '/api/projects';

  constructor(private http: HttpClient) {}

  getAnalyses(projectName: string): Observable<Analysis[]> {
    return this.http.get<Analysis[]>(`${this.apiUrl}/${projectName}/analyses`);
  }

  getAnalysis(projectName: string, analysisId: number): Observable<Analysis> {
    return this.http.get<Analysis>(`${this.apiUrl}/${projectName}/analyses/${analysisId}`);
  }

  createAnalysis(projectName: string, analysis: Analysis): Observable<Analysis> {
    return this.http.post<Analysis>(`${this.apiUrl}/${projectName}/analyses`, analysis);
  }

  deleteAnalysis(projectName: string, analysisId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectName}/analyses/${analysisId}`);
  }

  getSensitivityAnalysis(projectName: string, analysisId: number, criterionId: number): Observable<SensitivityResult> {
    return this.http.get<SensitivityResult>(
      `${this.apiUrl}/${projectName}/analyses/${analysisId}/sensitivity/${criterionId}`
    );
  }
}
