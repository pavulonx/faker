import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-endpoint-tile',
  templateUrl: './endpoint-tile.component.html',
  styleUrls: ['./endpoint-tile.component.css']
})
export class EndpointTileComponent implements OnInit {

  @Input()
  endpoint: Endpoint;

  @Input()
  enabled: boolean;

  constructor() {
  }

  ngOnInit() {
  }

  unseenEvents() {
    return false;
  }

  endpointCreationDate() {
    // todo: format humn-friendly style
    return this.endpoint.createdAt;
  }
}
