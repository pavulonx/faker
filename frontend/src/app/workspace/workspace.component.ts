import {Component, OnInit} from '@angular/core';
import {WebsocketService} from "../websocket.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {EndpointModalComponent} from "./endpoint-modal/endpoint-modal.component";

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: ['./workspace.component.css']
})
export class WorkspaceComponent implements OnInit {

  constructor(private ws: WebsocketService, private modalService: NgbModal) {
    // ws.getUpdates$.subscribe(e => console.log(e))
  }

  ngOnInit() {
  }

  openEndpointModal() {
    const modalRef = this.modalService.open(EndpointModalComponent);
    modalRef.componentInstance.id = 10; // should be the id

    modalRef.result.then((result) => {
      console.log(result);
    }).catch((error) => {
      console.log(error);
    });
  }


}
