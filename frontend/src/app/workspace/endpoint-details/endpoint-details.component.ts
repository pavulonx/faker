import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../api.service';
import {flatMap, map} from 'rxjs/operators';

@Component({
  selector: 'app-endpoint-details',
  templateUrl: './endpoint-details.component.html',
  styleUrls: ['./endpoint-details.component.css']
})
export class EndpointDetailsComponent implements OnInit {

  endpointInfo: Endpoint = null;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
  }

  ngOnInit() {
    this.route.params
      .pipe(
        map(params => params['endpointUuid']),
        flatMap(uuid => this.apiService.getEndpoint(uuid))
      ).subscribe(endpoint => this.endpointInfo = endpoint);
  }

}
