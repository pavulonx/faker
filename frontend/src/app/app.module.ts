import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";


import {AppComponent} from './app.component';
import {WebsocketService} from "./websocket.service";
import { WorkspaceComponent } from './workspace/workspace.component';


@NgModule({
  declarations: [
    AppComponent,
    WorkspaceComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    WebsocketService,
    HttpClientModule,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
