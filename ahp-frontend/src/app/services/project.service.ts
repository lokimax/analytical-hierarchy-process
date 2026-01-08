import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Project {
  id?: number;
  name: string;
  beschreibung: string;
  clientId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private baseUrl = 'http://localhost:9000/api/clients';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getAuthToken();
    return new HttpHeaders({
      'Authorization': token || ''
    });
  }

  private getApiUrl(): string {
    const user = this.authService.getCurrentUser()();
    return `${this.baseUrl}/${user?.nickname}/projects`;
  }

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.getApiUrl(), { headers: this.getHeaders() });
  }

  getProject(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.getApiUrl()}/${id}`, { headers: this.getHeaders() });
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.getApiUrl(), project, { headers: this.getHeaders() });
  }

  updateProject(id: number, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.getApiUrl()}/${id}`, project, { headers: this.getHeaders() });
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.getApiUrl()}/${id}`, { headers: this.getHeaders() });
  }
}

