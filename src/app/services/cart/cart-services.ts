import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductResponseDto } from '../model/product/product-response-dto';
import { Observable } from 'rxjs';
import { CartResponseDto } from '../model/order/cart-response-dto';

@Injectable({
  providedIn: 'root'
})
export class CartServices {

  private readonly cartServicePath = 'http://localhost:1237/CartVault';
  private readonly ACCESS = 'access_token';
  private readonly REFRESH = 'refresh_token';

  constructor(private http: HttpClient) { }

  addToCart(productId: string, quantity: number): Observable<any> {
    const token = localStorage.getItem(this.ACCESS);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const url = `${this.cartServicePath}/cart/add?productId=${productId}&quantity=${quantity}`;
    return this.http.post(url, {}, { headers });
  }

  getCart(): Observable<any> {
    const token = localStorage.getItem(this.ACCESS);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const url = `${this.cartServicePath}/cart`;
    return this.http.get(url, { headers });
  }

  getCartItemCount(): Observable<number> {
    const token = localStorage.getItem(this.ACCESS);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const url = `${this.cartServicePath}/cart/cartItemCount`;
    return this.http.get<number>(url, { headers });
  }

  getCartForUser(): Observable<CartResponseDto> {
    const token = localStorage.getItem(this.ACCESS);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const url = `${this.cartServicePath}/cart`;
    return this.http.get<CartResponseDto>(url, { headers });
  }
}