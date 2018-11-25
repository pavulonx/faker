import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HostProviderInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({
      url: this.serviceAddress(req.url)
    });
    console.debug("Api call: " + JSON.stringify(req));
    return next.handle(req);
  }

  private serviceAddress(url: string): string {
    if (url.includes('ws://'))
      return url;
    else
      return 'http://127.0.0.1:8811/' + url;
  }

}
