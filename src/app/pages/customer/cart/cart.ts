import { Component, OnInit } from '@angular/core';
import { CartServices } from '../../../services/cart/cart-services';
import { ProductServices } from '../../../services/product/product-services';
import { forkJoin } from 'rxjs';
import { ProductResponseDto } from '../../../services/model/product/product-response-dto';
import { CartResponseDto } from '../../../services/model/order/cart-response-dto';
import { AddressResponseDto } from '../../../services/model/order/address-response-dto';
import { AddressRequestDto } from '../../../services/model/order/address-request-dto';
import { Address } from '../../../services/order/address';

@Component({
  selector: 'app-cart',
  standalone: false,
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class Cart implements OnInit {

  cartResponse: CartResponseDto = { cartId: '', userId: '', cartItems: [] };
  productResponse: ProductResponseDto[] = [];
  addressResponse: AddressResponseDto[] = [];

  totalPrice = 0;
  shipping = 50;
  paymentMethod = 'card';

  // address form model
  addressRequest: AddressRequestDto = {
    type: 'Home',
    fullName: '',
    line1: '',
    line2: '',
    city: '',
    state: '',
    postalCode: '',
    country: 'India',
    phone: ''
  };

  selectedAddress: AddressResponseDto | null = null;

  constructor(
    private cartService: CartServices,
    private productService: ProductServices,
    private addressService: Address
  ) { }

  ngOnInit(): void {
    this.getCartForUser();
    this.getListOfAddress();
  }

  /** =======================
   *  GET CART + PRODUCT DETAILS
   *  ======================= */
  getCartForUser() {
    this.cartService.getCartForUser().subscribe({
      next: (response: CartResponseDto) => {

        this.cartResponse = response;

        // if cart is empty, stop
        if (!response.cartItems?.length) {
          this.productResponse = [];
          this.cartResponse.cartItems = [];
          return;
        }

        // fetch product details
        const productCalls = response.cartItems.map(item =>
          this.productService.getProductById(item.productId)
        );

        forkJoin(productCalls).subscribe({
          next: (products) => {
            this.productResponse = products;

            // merge product into cart
            this.cartResponse.cartItems = this.cartResponse.cartItems.map((item, index) => ({
              ...item,
              quantity: item.quantity || 1,
              productDetails: products[index]
            }));

            this.calculateTotalPrice();
          }
        });
      },
      error: (err) => console.error('Failed to fetch cart:', err)
    });
  }

  /** ============
   * CALCULATIONS
   * ============ */
  calculateTotalPrice() {
    this.totalPrice = this.cartResponse.cartItems.reduce((sum, item, index) => {
      const price = this.productResponse[index]?.price || 0;
      return sum + item.quantity * price;
    }, 0);
  }

  increaseQuantity(i: number) {
    this.cartResponse.cartItems[i].quantity++;
    this.calculateTotalPrice();
  }

  decreaseQuantity(i: number) {
    if (this.cartResponse.cartItems[i].quantity > 1) {
      this.cartResponse.cartItems[i].quantity--;
      this.calculateTotalPrice();
    }
  }

  removeItem(i: number) {
    this.cartResponse.cartItems.splice(i, 1);
    this.productResponse.splice(i, 1);
    this.calculateTotalPrice();
  }

  getSubtotal(): number {
    return this.totalPrice;
  }

  /** =======================
   *   ADDRESS SECTION
   *  ======================= */

  getListOfAddress() {
    this.addressService.getAddress().subscribe({
      next: (response: AddressResponseDto[] | AddressResponseDto[][]) => {
        // If it's nested array, flatten it
        if (Array.isArray(response[0])) {
          this.addressResponse = (response as AddressResponseDto[][]).flat();
        } else {
          this.addressResponse = response as AddressResponseDto[];
        }
        console.log('Fetched address:', this.addressResponse);
      },
      error: (err) => {
        console.error('Failed to fetch address:', err);
      }
    });

  }


  selectAddress(addr: AddressResponseDto) {
    this.selectedAddress = addr;
  }

  addressCreate() {

  }

  addOrUpdateAddress() {

  }

  /** =======================
   * CHECKOUT
   * ======================= */
  checkout() {
    console.log("Checkout clicked", {
      items: this.cartResponse.cartItems,
      address: this.selectedAddress,
      paymentMethod: this.paymentMethod
    });

    alert("Proceeding to payment!");
  }

  cancelAddressForm() { }

  startAddressCreation() { }

  editAddress(addr: AddressResponseDto) { }
}