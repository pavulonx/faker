import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-endpoint-tile',
  templateUrl: './endpoint-tile.component.html',
  styleUrls: ['./endpoint-tile.component.css']
})
export class EndpointTileComponent implements OnInit {

  @Input()
  endpoint: any;

  constructor() {

  }

  ngOnInit() {
    console.log(this.endpoint)
  }

  unseenEvents() {
    return false;
  }

}
