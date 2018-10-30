import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";


import {AppComponent} from './app.component';
import {WebsocketService} from "./websocket.service";


@NgModule({
  declarations: [
    AppComponent
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
