import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AddressResponseDto } from '../model/order/address-response-dto';

@Injectable({
  providedIn: 'root'
})
export class Address {

  private readonly addressServicePath = 'http://localhost:1237/CartVault';
  private readonly ACCESS = 'access_token';
  private readonly REFRESH = 'refresh_token';

  constructor(private http: HttpClient) { }

  getAddress(): Observable<AddressResponseDto[]> {
    const token = localStorage.getItem(this.ACCESS);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const url = `${this.addressServicePath}/address`;
    return this.http.get<AddressResponseDto[]>(url, { headers });
  }
}
