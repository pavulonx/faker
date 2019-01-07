import {Component, OnInit} from '@angular/core';
import {WebsocketService} from '../websocket.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {EndpointModalComponent} from './endpoint-modal/endpoint-modal.component';
import {ApiService} from '../api.service';
import {flatMap, map, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import * as R from 'ramda';

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: ['./workspace.component.css']
})
export class WorkspaceComponent implements OnInit {

  workspace: Workspace;
  wsUpdates: Observable<any>;

  filteredEndpoints: Endpoint[];

  constructor(private api: ApiService, private ws: WebsocketService, private modalService: NgbModal, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit() {
    this.route.params
      .pipe(
        map(params => params['workspaceName']),
        flatMap(workspaceName => this.api.getWorkspace(workspaceName)),
        tap(workspace => this.workspace = workspace),
        tap(workspace => this.filteredEndpoints = workspace.endpoints),
        tap(workspace => this.wsUpdates = this.ws.getUpdates$(workspace.name)),
        // tap(_ => this.wsUpdates.subscribe(e => this.handleEvent(e)))  //fixme: for testing
      ).subscribe();
  }

  openEndpointModal() {
    const modalRef = this.modalService.open(EndpointModalComponent);
    modalRef.result.then((requestedEndpoint: Endpoint) => {
      console.log(requestedEndpoint);
      this.api.addEndpoint(this.workspace.name, requestedEndpoint).subscribe(
        res => this.workspace.endpoints.push(res)
      );
    }).catch((error) => {
      console.error(error);
    });
  }

  tileEnabled(endpointId: string) {
    return this.router.url.includes(endpointId);
  }

  getEndpoints() {
    return this.filteredEndpoints;
  }

  filterEndpoints(input) {
    this.filteredEndpoints = this.workspace.endpoints
      .filter(e =>
        R.valuesIn(e).filter(p => p.toString().toUpperCase().includes(input.value.toUpperCase())).length > 0
      );
  }
}
