import {Injectable} from '@angular/core';
import {uuid} from 'uuid/v4';

const UUID_KEY: string = "client_uuid";

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor() {
  }

  getUuid() {
    const clientUuid = localStorage.getItem(UUID_KEY);
    if (clientUuid == null) {
      const newUuid = uuid.v4();
      localStorage.setItem(UUID_KEY, newUuid);
      return newUuid;

    } else return clientUuid;
  }

}
