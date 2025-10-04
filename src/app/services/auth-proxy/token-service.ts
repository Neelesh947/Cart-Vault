import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly ACCESS = 'access_token';
  private readonly REFRESH = 'refresh_token';

  constructor(private jwtHelper: JwtHelperService) { }

  setTokens(access: string, refresh: string): void {
    localStorage.setItem(this.ACCESS, access);
    localStorage.setItem(this.REFRESH, refresh);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH);
  }

  clear(): void {
    localStorage.removeItem(this.ACCESS);
    localStorage.removeItem(this.REFRESH);
  }

  decodeAccessToken(): any {
    const token = this.getAccessToken();
    if (!token) return null;

    try {
      const decoded = this.jwtHelper.decodeToken(token);
      return decoded;
    } catch (e) {
      console.error('Failed to decode token:', e);
      return null;
    }
  }

  getUsername(): string | null {
    const decoded = this.decodeAccessToken();
    return decoded?.preferred_username ?? null;
  }

  getUserId(): string | null {
    const decoded = this.decodeAccessToken();
    return decoded?.sub ?? null;
  }

  isTokenValid(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;

    const isExpired = this.jwtHelper.isTokenExpired(token);
    if (isExpired) {
      this.clear();
      return false;
    }
    return true;
  }

  isTokenNotValid(): boolean {
    return !this.isTokenValid();
  }

  getTokenExpiryTime(): number | null {
    const decode = this.decodeAccessToken();
    if(!decode || !decode.exp) return null;
    return decode.exp * 1000;
  }
  
}
