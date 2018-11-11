import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  // private apiUrl: String = 'localhost:8280/';

  constructor(private http: HttpClient) {
  }

  get endpoints() {
    return this.http.get('');
  }

}
