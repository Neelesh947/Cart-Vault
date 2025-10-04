import { Component } from '@angular/core';
import { UserCredentialsDto } from '../../services/model/keycloak/user-credentials-dto';
import { Router } from '@angular/router';
import { AuthProxyService } from '../../services/auth-proxy/auth-proxy-service';
import { TokenService } from '../../services/auth-proxy/token-service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  authRequest: UserCredentialsDto = { userName: '', password: '' };
  loginError: string | null = null;

  constructor(private router: Router, private authService: AuthProxyService, private tokenService: TokenService) { }

  onLogin() {
    this.authService.login(this.authRequest).subscribe({
      next: (response) => {
        this.tokenService.setTokens(response.access_token, response.refresh_token);
        this.authService.setLoggedIn(true);

        const isSuperAdmin = this.authService.hasRole('super_admin');
        const isVendor = this.authService.hasRole('vendor');
        const isCustomer = this.authService.hasRole('customer');
        if(isSuperAdmin){
            this.router.navigate(['#']);
          } else if(isVendor){
            this.router.navigate(['#']);
          } else if(isCustomer){
            this.router.navigate(['/customer-home-page']);
          } else{
            alert("Unauthorized role");
          }
      },
      error: (err) => {
        this.loginError = 'Invalid username or password';
        this.authService.setLoggedIn(false);
      }
    })

  }
}
