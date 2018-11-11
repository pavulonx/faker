import {HTTP_INTERCEPTORS} from '@angular/common/http';

import {HostProviderInterceptor} from './host-provider-interceptor';

export const httpInterceptorProviders = [
  {provide: HTTP_INTERCEPTORS, useClass: HostProviderInterceptor, multi: true},
];
