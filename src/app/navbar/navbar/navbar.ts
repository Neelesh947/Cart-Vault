import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthProxyService } from '../../services/auth-proxy/auth-proxy-service';
import { Subscription } from 'rxjs';
import { TokenService } from '../../services/auth-proxy/token-service';
import { Router } from '@angular/router';
import { CartServices } from '../../services/cart/cart-services';

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
    private router: Router,
    private cartService: CartServices
  ) { }

  ngOnInit(): void {
    this.authSubscription = this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;

      if (status) {
        const decoded = this.token.decodeAccessToken();
        this.username = decoded?.preferred_username ?? 'user';
        const roles: string[] = decoded?.realm_access?.roles ?? [];
        const ignoredRoles = ['offline_access', 'uma_authorization', 'default-roles-cartvault'];
        this.role = roles.find(r => !ignoredRoles.includes(r)) ?? '';
        // Optional: get cart count if role is customer
        if (this.role === 'customer') {
          this.cartService.getCartItemCount().subscribe({
            next: (count: number) => {
              this.cartItemCount = count;
            },
            error: (err) => {
              console.error('Failed to fetch cart count', err);
              this.cartItemCount = 0;
            }
          });
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
        window.location.reload();
      },
      error: (err) => {
        console.error('Logout failed:', err);
        alert('Logout failed: ' + (err.error || 'Unknown error'));
      }
    });
  }

  goToCart(){
    console.log('cart clicked')
    this.router.navigate(['/customer-home-page/cart']);
  }
}