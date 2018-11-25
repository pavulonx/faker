import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {
  }

  getEndpoint(workspaceName: string, endpointUuid: string): Observable<Endpoint> {
    return this.http.get<Endpoint>('api/endpoint/' + workspaceName + '/' + endpointUuid);
  }

  getEndpoints(workspaceName: string): Observable<Endpoint[]> {
    return this.http.get<Endpoint[]>('api/endpoint/' + workspaceName);
  }

  getWorkspace(workspaceName: string): Observable<Workspace> {
    return this.http.get<Workspace>('api/workspace/' + workspaceName);
  }

}
