import { Component } from '@angular/core';
import {WebsocketService} from "./websocket.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';

  constructor(private ws: WebsocketService){
    ws.getUpdates$.subscribe(e => console.log(e))
  }
}
