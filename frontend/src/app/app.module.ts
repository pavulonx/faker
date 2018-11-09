import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {WorkspaceComponent} from "./workspace/workspace.component";
import {WebsocketService} from "./websocket.service";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EndpointModalComponent} from './workspace/endpoint-modal/endpoint-modal.component';


@NgModule({
  declarations: [
    AppComponent,
    WorkspaceComponent,
    EndpointModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule
  ],
  providers: [
    WebsocketService,
    HttpClientModule,
  ],
  bootstrap: [
    AppComponent
  ],
  entryComponents: [
    EndpointModalComponent
  ]
})
export class AppModule {
}
