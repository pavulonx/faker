import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {stringify} from 'querystring';


@Injectable()
export class WebsocketService {

  private wsSubjects = new Map<string, WebSocketSubject<any>>();

  private wsUrl = 'ws://localhost:8280/notifications/';

  constructor() {
  }

  public wsSubject(workspaceName: string): WebSocketSubject<any> {
    if (!this.wsSubjects.get(workspaceName) || this.wsSubjects.get(workspaceName).closed) {
      this.wsSubjects = this.wsSubjects.set(workspaceName, this.connect(workspaceName));
    }
    console.debug(this.wsSubjects);
    return this.wsSubjects.get(workspaceName);
  }

  private connect(workspaceName: string) {
    return webSocket({
        url: this.getUrl(workspaceName),
        deserializer: this.stringDeserializer()
      }
    );
  }

  getUpdates$(workspaceName: string): Observable<any> {
    return this.wsSubject(workspaceName).asObservable();
  }

  public send(workspaceName, msg) {
    console.debug('Sending msg to ' + workspaceName + '' + msg);
    return this.wsSubject(workspaceName).next(msg);
  }

  private getUrl(workspaceName: string): string {
    return this.wsUrl + workspaceName;
  }

  stringDeserializer() {
    return x => stringify(x);
  }

}
