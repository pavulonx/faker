import {Component, OnInit} from '@angular/core';
import {WebsocketService} from "../websocket.service";

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: ['./workspace.component.css']
})
export class WorkspaceComponent implements OnInit {

  constructor(private ws: WebsocketService) {
    // ws.getUpdates$.subscribe(e => console.log(e))
  }

  ngOnInit() {
  }

}
