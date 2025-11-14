import { Component } from '@angular/core';
import { ProductResponseDto } from '../../../services/model/product/product-response-dto';
import { ProductServices } from '../../../services/product/product-services';
import { CartServices } from '../../../services/cart/cart-services';
import { TokenService } from '../../../services/auth-proxy/token-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product',
  standalone: false,
  templateUrl: './product.html',
  styleUrl: './product.css'
})
export class Product {

  products: ProductResponseDto[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  username: string = ''

  constructor(private productService: ProductServices, private cartService: CartServices, private router: Router, private token: TokenService) { }

  ngOnInit(): void {
    this.loadproducts();
  }

  loadproducts() {
    this.productService.getAllProducts().subscribe({
      next: (data: ProductResponseDto[]) => {
        this.products = data.map(product => ({ ...product, quantity: 0 }));
        console.log(data)
      },
      error: (err) => {
        console.error('Error fetching products:', err);
        this.errorMessage = 'Failed to load products. Please try again later.';
        this.isLoading = false;
      }
    })
  }

  increaseQuantity(index: number): void {
    if (this.products[index]) {
      this.products[index].quantity = (this.products[index].quantity || 0) + 1;
    }
  }

  decreaseQuantity(index: number): void {
    if (this.products[index] && this.products[index].quantity > 0) {
      this.products[index].quantity--;
    }
  }

  addToCart(product: ProductResponseDto): void {
    this.cartService.addToCart(product.id, product.quantity).subscribe({
      next: (data) => {
        console.log('Product added to cart', data);
        product.quantity = 0;
        alert('Product has been added to Cart');
      },
      error: (err) => {
        console.error('Error adding to cart', err);
        this.token.clear();
        this.router.navigate(['login']);
      }
    })
  }
}
