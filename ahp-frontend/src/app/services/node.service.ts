import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Node {
  id?: number;
  name: string;
  beschreibung?: string;
  content?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  private baseUrl = 'http://localhost:9000/api/clients';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getApiUrl(projectName: string): string {
    const user = this.authService.getCurrentUser()();
    return `${this.baseUrl}/${user?.nickname}/projects/${projectName}/nodes`;
  }

  getNodes(projectName: string): Observable<Node[]> {
    return this.http.get<Node[]>(this.getApiUrl(projectName));
  }

  createNode(projectName: string, node: Node): Observable<Node> {
    return this.http.post<Node>(this.getApiUrl(projectName), node);
  }

  deleteNode(projectName: string, nodeName: string): Observable<void> {
    return this.http.delete<void>(`${this.getApiUrl(projectName)}/${nodeName}`);
  }
}
