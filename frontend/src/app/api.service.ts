import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {
  }

  getEndpoint(workspaceName: string, endpointId: string): Observable<Endpoint> {
    return this.http.get<Endpoint>('endpoint/' + workspaceName + '/' + endpointId);
  }

  getCalls(workspaceName: string, endpointId: string): Observable<Call[]> {
    return this.http.get<Call[]>('call/' + workspaceName + '/' + endpointId);
  }

  addEndpoint(workspaceName: string, endpointRequest: Endpoint): Observable<Endpoint> {
    return this.http.post<Endpoint>('endpoint/' + workspaceName, endpointRequest);
  }

  deleteEndpoint(workspaceName: string, endpointId: string): Observable<Endpoint> {
    return this.http.delete<Endpoint>('endpoint/' + workspaceName + '/' + endpointId);
  }

  getEndpoints(workspaceName: string): Observable<Endpoint[]> {
    return this.http.get<Endpoint[]>('endpoint/' + workspaceName);
  }

  getWorkspace(workspaceName: string): Observable<Workspace> {
    return this.http.get<Workspace>('workspace/' + workspaceName);
  }

  addWorkspace(workspaceRequest: WorkspaceRequest): Observable<Workspace> {
    return this.http.post<Workspace>('workspace', workspaceRequest);
  }
}
