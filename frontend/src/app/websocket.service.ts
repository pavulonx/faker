import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {stringify} from 'querystring';
import {LocalStorageService} from './local-storage.service';


@Injectable()
export class WebsocketService {

  private _wsSubject: WebSocketSubject<any>;
  private wsUrl = 'ws://localhost:8280/notifications/';

  constructor(private localStorageService: LocalStorageService) {
  }

  public get wsSubject(): WebSocketSubject<any> {
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
    return this.wsUrl + this.localStorageService.getUuid();
  }

  stringDeserializer() {
    return x => stringify(x);
  }

}
