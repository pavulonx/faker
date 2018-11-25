import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../api.service';
import {flatMap, map} from 'rxjs/operators';

@Component({
  selector: 'app-endpoint-details',
  templateUrl: './endpoint-details.component.html',
  styleUrls: ['./endpoint-details.component.css']
})
export class EndpointDetailsComponent implements OnInit {

  endpoint: Endpoint;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private router: Router) {
  }

  ngOnInit() {
    this.route.parent.params
      .pipe(
        map(params => params['workspaceName']),
        flatMap(workspaceName =>
          this.route.params.pipe(
            map(params => params['endpointId']),
            flatMap(endpointId => this.apiService.getEndpoint(workspaceName, endpointId)),
          )
        ))
      .subscribe(
        endpoint => this.endpoint = endpoint,
        // err => this.route.parent.url.subscribe(url => this.router.navigate([url]))
      );
  }

}
