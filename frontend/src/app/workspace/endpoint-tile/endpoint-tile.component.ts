import {Component, Input, OnInit} from '@angular/core';
import {WebsocketService} from "../../websocket.service";
import {log} from "util";
import {tap} from "rxjs/operators";

@Component({
  selector: 'app-endpoint-tile',
  templateUrl: './endpoint-tile.component.html',
  styleUrls: ['./endpoint-tile.component.css']
})
export class EndpointTileComponent implements OnInit {

  @Input() endpoint: Endpoint;
  @Input() workspaceName: string; //fixme pass context wrapped in service
  @Input() enabled: boolean;

  @Input() unseenEvents: boolean = false;

  newEvents: Event[];

  constructor(private wsService: WebsocketService) {
  }

  ngOnInit() {
    this.wsService.getUpdates$(this.workspaceName).pipe(tap(e => this.handleEvent(e))).subscribe() //fixme
  }

  endpointCreationDate() {
    // todo: format human-friendly style
    return this.endpoint.createdAt;
  }

  private handleEvent(event: ApplicationEvent) { //fixme
    console.log("EndpointTileComponent.handleEvent" + this.endpoint.endpointId);
    console.log("EndpointTileComponent.handleEvent" + JSON.stringify(event));
    if (event.entityType && event.entityType == 'NewCall') {
      const newCallEvent: NewCall = event as NewCall;
      if (newCallEvent.call.endpointId === this.endpoint.endpointId)
        this.unseenEvents = true;
    }
  }
}
