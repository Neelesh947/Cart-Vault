import { Component } from '@angular/core';
import { AuthProxyService } from '../../services/auth-proxy/auth-proxy-service';
import { RegisterUserDto } from '../../services/model/keycloak/register-user-dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-account',
  standalone: false,
  templateUrl: './create-account.html',
  styleUrl: './create-account.css'
})
export class CreateAccount {

  user = {
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    password: '',
  };

  confirmPassword = '';
  userType: 'customer' | 'vendor' | '' = '';

  constructor(private authService: AuthProxyService, private router: Router) { }

  onRegister() {
    if (this.user.password !== this.confirmPassword) {
      alert("Password did not match");
      return;
    }

    const payload: RegisterUserDto = {
      username: this.user.username,
      email: this.user.email,
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      credentials: [
        {
          type: 'password',
          value: this.user.password,
          temporary: false
        }
      ]
    };

    this.authService.createUser(payload, this.userType).subscribe({
      next: (response: any) => {
        const userId = response.userId;
        console.log(userId)
        alert('User created successfully.');
        this.router.navigate(['/verify-otp', userId]);
      },
      error: (err) => {
        console.error('Error creating user:', err);
        alert('Failed to create user.');
      }
    })

  }
}
