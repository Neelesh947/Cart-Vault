import { TestBed } from '@angular/core/testing';

import { AuthProxyService } from './auth-proxy-service';

describe('AuthProxyService', () => {
  let service: AuthProxyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthProxyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
