import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Address {
  
  private readonly cartServicePath = 'http://localhost:1237/CartVault';
  private readonly ACCESS = 'access_token';
  private readonly REFRESH = 'refresh_token';

  constructor(private http: HttpClient) { }
}
