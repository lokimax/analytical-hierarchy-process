import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Node {
  id?: number;
  name: string;
  beschreibung?: string;
  content?: string;
  projectId?: number;
}

export interface Connection {
  id?: number;
  sourceNodeName: string;
  targetNodeName: string;
  projectId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  private apiUrl = 'http://localhost:9000/api/projects';

  constructor(private http: HttpClient) {}

  getNodes(projectName: string): Observable<Node[]> {
    return this.http.get<Node[]>(`${this.apiUrl}/${projectName}/nodes`);
  }

  getNode(projectName: string, nodeName: string): Observable<Node> {
    return this.http.get<Node>(`${this.apiUrl}/${projectName}/nodes/${nodeName}`);
  }

  createNode(projectName: string, node: Node): Observable<Node> {
    return this.http.post<Node>(`${this.apiUrl}/${projectName}/nodes`, node);
  }

  deleteNode(projectName: string, nodeName: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectName}/nodes/${nodeName}`);
  }

  getConnections(projectName: string): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/${projectName}/connections`);
  }

  createConnection(projectName: string, connection: Connection): Observable<Connection> {
    return this.http.post<Connection>(`${this.apiUrl}/${projectName}/connections`, connection);
  }

  deleteConnection(projectName: string, connectionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectName}/connections/${connectionId}`);
  }
}
