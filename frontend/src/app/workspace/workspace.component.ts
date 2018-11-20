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

  endpoints: Endpoint[] = [
    {
      uuid: "uuid1", name: "asd", response: {code: 2137}
    }, {
      uuid: "uuid2", name: "asd", response: {code: 2137}
    }, {
      uuid: "uuid3", name: "asd", response: {code: 2137}
    }, {
      uuid: "uuid4", name: "asd", response: {code: 2137}
    }, {
      uuid: "uuid5", name: "asd", response: {code: 2137}
    }];

  constructor(private ws: WebsocketService, private modalService: NgbModal) {
    ws.getUpdates$.subscribe(e => console.log(e))
  }

  ngOnInit() {
  }

  openEndpointModal() {
    const modalRef = this.modalService.open(EndpointModalComponent);
    modalRef.result.then((result: Endpoint) => {
      console.log(result);
      this.endpoints.push(result)
    }).catch((error) => {
      console.log(error);
    });
  }

}
