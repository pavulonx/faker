import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {stringify} from "querystring";


@Injectable()
export class WebsocketService {

  private _wsSubject: WebSocketSubject<any>;
  private wsUrl = 'ws://localhost:8180/notifications/';
  private clientId = 'CHUJ';

  constructor() {
  }

  public get wsSubject(): WebSocketSubject<any> {
    console.log("wsSubjectXXXXXXXXXXXXX");
    if (!this._wsSubject || this._wsSubject.closed) {
      this._wsSubject = webSocket({
          url: this.url,
          deserializer: this.stringDeserializer()
        }
      );
    }
    return this._wsSubject;
  }

  get getUpdates$(): Observable<any> {
    return this.wsSubject.asObservable();
  }

  public send(msg) {
    console.log(msg);
    return this.wsSubject.next(msg);
  }

  private get url(): string {
    return this.wsUrl + this.clientId
  }

  stringDeserializer() {
    return x => stringify(x);
  }

}
