import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../api.service';
import {flatMap, map, tap} from 'rxjs/operators';

@Component({
  selector: 'app-endpoint-details',
  templateUrl: './endpoint-details.component.html',
  styleUrls: ['./endpoint-details.component.css']
})
export class EndpointDetailsComponent implements OnInit {

  endpoint: Endpoint = null;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
  }

  ngOnInit() {
    this.route.params
      .pipe(
        map(params => params['endpointId']),
        flatMap(uuid => this.apiService.getEndpoint('ws1', uuid))
      ).subscribe(endpoint => this.endpoint = endpoint);
  }

}
