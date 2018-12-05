import {Component, Input, OnInit} from '@angular/core';
import {WebsocketService} from "../../websocket.service";

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
    this.wsService.getUpdates$(this.workspaceName).subscribe(this.handleEvent) //fixme
  }

  endpointCreationDate() {
    // todo: format human-friendly style
    return this.endpoint.createdAt;
  }

  private handleEvent(event: Event) { //fixme
    if (event.type == 'NewCall' && event.type) {
      const newCallEvent: NewCall = event as NewCall;
      if (newCallEvent.call.endpointId === this.endpoint.endpointId)
        this.unseenEvents = true;
    }
  }
}
