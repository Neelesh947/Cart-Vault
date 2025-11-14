import { Component, OnInit } from '@angular/core';
import { CartServices } from '../../../services/cart/cart-services';
import { ProductServices } from '../../../services/product/product-services';
import { forkJoin } from 'rxjs';
import { ProductResponseDto } from '../../../services/model/product/product-response-dto';
import { CartResponseDto } from '../../../services/model/order/cart-response-dto';

@Component({
  selector: 'app-cart',
  standalone: false,
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class Cart implements OnInit {

  cartResponse: CartResponseDto = { cartId: '', userId: '', cartItems: [] };
  totalPrice: number = 0;
  shipping: number = 50;
  paymentMethod: string = 'card';

  // Store products mapped by cart item
  productResponse: ProductResponseDto[] = [];

  address = {
    name: '',
    street: '',
    city: '',
    state: '',
    zip: ''
  };

  constructor(private cartService: CartServices, private productService: ProductServices) { }

  ngOnInit(): void {
    this.getCartForUser();
  }

  getCartForUser() {
    this.cartService.getCartForUser().subscribe({
      next: (response: CartResponseDto) => {
        this.cartResponse = response;

        if (!this.cartResponse.cartItems || this.cartResponse.cartItems.length === 0) {
          this.cartResponse.cartItems = [];
          return;
        }

        // Fetch product details for each cart item
        const productObservables = this.cartResponse.cartItems.map(item =>
          this.productService.getProductById(item.productId)
        );

        forkJoin(productObservables).subscribe({
          next: (products: ProductResponseDto[]) => {
            this.productResponse = products;

            // Merge product details into cart items
            this.cartResponse.cartItems = this.cartResponse.cartItems.map((item, index) => ({
              ...item,
              productDetails: products[index], // store product info inside each cart item
              quantity: item.quantity || 1
            }));

            this.calculateTotalPrice();
          },
          error: (err) => {
            console.error('Failed to fetch product details:', err);
          }
        });
      },
      error: (err) => {
        console.error('Failed to fetch cart:', err);
      }
    });
  }

  calculateTotalPrice() { }

  increaseQuantity(index: number) {
    this.cartResponse.cartItems[index].quantity++;
    this.calculateTotalPrice();
  }

  decreaseQuantity(index: number) {
    if (this.cartResponse.cartItems[index].quantity > 1) {
      this.cartResponse.cartItems[index].quantity--;
      this.calculateTotalPrice();
    }
  }

  removeItem(index: number) {

  }

  getSubtotal(): number {
    return this.cartResponse.cartItems.reduce((sum, item, index) => {
      const product = this.productResponse[index];
      return sum + (item.quantity * (product?.price || 0));
    }, 0);
  }


  checkout() {
    console.log('Proceeding to checkout', this.cartResponse.cartItems, this.address, this.paymentMethod);
    alert('Checkout clicked!');
  }

}