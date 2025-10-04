import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TokenService } from './token-service';
import { UserCredentialsDto } from '../model/keycloak/user-credentials-dto';
import { BehaviorSubject, Observable } from 'rxjs';
import { KeycloakTokenResponseDto } from '../model/keycloak/keycloak-token-response-dto';
import { RegisterUserDto } from '../model/keycloak/register-user-dto';

@Injectable({
  providedIn: 'root'
})
export class AuthProxyService {

  private readonly KeycloakServicePath = 'http://localhost:1234/CartVault';

  constructor(private http: HttpClient, private token: TokenService) { }

  private loggedIn = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.loggedIn.asObservable();

  /**
   * sends login credentials to keycloak and return a token response
   * @param UserCredentialsDto 
   */
  login(credentials: UserCredentialsDto): Observable<KeycloakTokenResponseDto> {
    const url = `${this.KeycloakServicePath}/login`;
    return this.http.post<KeycloakTokenResponseDto>(url, credentials);
  }

  /**
   * Set logged in or not
   */
  setLoggedIn(value: boolean) {
    this.loggedIn.next(value);
  }

  /**
   * Create user in Keycloak with a specific role.
   * @param user The Keycloak user DTO to send in the body.
   * @param role The role to assign to the user (from path variable).
   * @returns 
   */
  createUser(user: RegisterUserDto, role: string): Observable<RegisterUserDto> {
    const url = `${this.KeycloakServicePath}/create-user/${role}`;
    return this.http.post<RegisterUserDto>(url, user);
  }

  /**
   * find out the role in from the token
   * @param role :- user roles
   * @returns 
   */
  hasRole(role: string): boolean {
    const token = this.token.decodeAccessToken();
    return token?.realm_access?.roles?.includes(role);
  }

  /**
   * Logout user from the dashboard
   * @param userId 
   * @returns 
   */
  logoutUser(userId: string): Observable<any> {
    const url = `${this.KeycloakServicePath}/users/${userId}/logout`;
    return this.http.post(url, null, { responseType: 'text' });
  }

  /**
   * verify otp and enable user
   * @param userId 
   * @param otp 
   * @returns 
   */
  verifyOtp(userId: string, otp: string): Observable<any> {
    const url = `${this.KeycloakServicePath}/${userId}/verify-otp?otp=${otp}`;
    return this.http.post(url, null, { responseType: 'text' });
  }
}
