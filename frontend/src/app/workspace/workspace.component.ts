import {Component, OnInit} from '@angular/core';
import {WebsocketService} from '../websocket.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {EndpointModalComponent} from './endpoint-modal/endpoint-modal.component';
import {ApiService} from '../api.service';
import {flatMap, map} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: ['./workspace.component.css']
})
export class WorkspaceComponent implements OnInit {

  private workspace: Workspace;

  constructor(private api: ApiService, private ws: WebsocketService, private modalService: NgbModal, private route: ActivatedRoute) {
    ws.getUpdates$.subscribe(e => console.log(e));
  }

  ngOnInit() {
    this.route.params
      .pipe(
        map(params => params['workspaceName']),
        flatMap(workspaceName => this.api.getWorkspace(workspaceName))
      ).subscribe(workspace => this.workspace = workspace);
  }

  openEndpointModal() {
    const modalRef = this.modalService.open(EndpointModalComponent);
    modalRef.result.then((result: Endpoint) => {
      console.log(result);
      this.workspace.endpoints.push(result);
    }).catch((error) => {
      console.log(error);
    });
  }

}
