import {Injectable} from '@angular/core';
import { v4 as uuid } from 'uuid';

const UUID_KEY: string = 'client_uuid';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor() {
  }

  getUuid() {
    const clientUuid = localStorage.getItem(UUID_KEY);
    if (clientUuid == null) {
      const newUuid = uuid();
      localStorage.setItem(UUID_KEY, newUuid);
      return newUuid;
    } else return clientUuid;
  }

}
