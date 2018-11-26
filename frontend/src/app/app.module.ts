import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {WorkspaceComponent} from './workspace/workspace.component';
import {WebsocketService} from './websocket.service';
import {HttpClientModule} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EndpointModalComponent} from './workspace/endpoint-modal/endpoint-modal.component';
import {EndpointTileComponent} from './workspace/endpoint-tile/endpoint-tile.component';
import {LocalStorageService} from './local-storage.service';
import {AboutComponent} from './about/about.component';
import {EndpointDetailsComponent} from './workspace/endpoint-details/endpoint-details.component';
import {ApiService} from './api.service';
import {httpInterceptorProviders} from './http-interceptors';
import { WorkspaceSelectComponent } from './workspace-select/workspace-select.component';


@NgModule({
  declarations: [
    AppComponent,
    WorkspaceComponent,
    EndpointModalComponent,
    EndpointTileComponent,
    AboutComponent,
    EndpointDetailsComponent,
    WorkspaceSelectComponent
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
    ApiService,
    LocalStorageService,
    HttpClientModule,
    httpInterceptorProviders
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
