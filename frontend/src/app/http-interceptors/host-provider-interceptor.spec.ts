import { TestBed } from '@angular/core/testing';

import { HostProviderInterceptor } from './host-provider-interceptor.service';

describe('HostProviderInterceptorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: HostProviderInterceptor = TestBed.get(HostProviderInterceptor);
    expect(service).toBeTruthy();
  });
});
