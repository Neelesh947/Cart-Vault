import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthProxyService } from '../../services/auth-proxy/auth-proxy-service';
import { Subscription } from 'rxjs';
import { TokenService } from '../../services/auth-proxy/token-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnInit, OnDestroy {

  isLoggedIn: boolean = false;
  username: string = '';
  role: string = '';
  cartItemCount: number = 0;

  private authSubscription!: Subscription;

  constructor(
    private authService: AuthProxyService,
    private token: TokenService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authSubscription = this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;

      if (status) {
        const decoded = this.token.decodeAccessToken();
        this.username = decoded?.preferred_username ?? 'user';
        this.role = decoded?.realm_access?.roles?.[0] ?? '';

        // Optional: get cart count if role is customer
        if (this.role === 'customer') {
          // this.cartItemCount = this.authService.getCartCount(); // Stub or implement as needed
        }
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  logout() {
    const userId = this.token.getUserId();
    if (!userId) {
      alert('Invalid user ID. Cannot logout.');
      return;
    }

    this.authService.logoutUser(userId).subscribe({
      next: () => {
        this.authService.setLoggedIn(false);
        this.token.clear();
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Logout failed:', err);
        alert('Logout failed: ' + (err.error || 'Unknown error'));
      }
    });
  }
}