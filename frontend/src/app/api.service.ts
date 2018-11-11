import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {
  }

  getEndpoint(endpointUuid: string): Observable<Endpoint> {
    return this.http.get<Endpoint>('endpoint/' + endpointUuid);
  }

  getEndpoints(): Observable<Endpoint[]> {
    return this.http.get<Endpoint[]>('endpoint/');
  }

}
