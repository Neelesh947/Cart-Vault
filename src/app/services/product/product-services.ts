import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductResponseDto } from '../model/product/product-response-dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductServices {

  private readonly productServicePath = 'http://localhost:1236/CartVault';

  constructor(private http: HttpClient) { }

  /**
   * Fetch all products from the product services
   */
  getAllProducts(): Observable<ProductResponseDto[]> {
    const url = `${this.productServicePath}/products`;
    return this.http.get<ProductResponseDto[]>(url);
  }

   /**
   * Fetch a product by its ID
   * @param id product id
   */
  getProductById(id: string): Observable<ProductResponseDto> {
    return this.http.get<ProductResponseDto>(`${this.productServicePath}/products/${id}`);
  }

  /**
   * Add a new product
   * @param product product data (ProductRequest)
   */
  addProduct(product: any): Observable<any> {
    return this.http.post(`${this.productServicePath}`, product);
  }

  /**
   * Update an existing product
   * @param id product id
   * @param product updated product data
   */
  updateProduct(id: string, product: any): Observable<any> {
    return this.http.put(`${this.productServicePath}/${id}`, product);
  }

  /**
   * Delete a product by ID
   * @param id product id
   */
  deleteProduct(id: string): Observable<any> {
    return this.http.delete(`${this.productServicePath}/${id}`);
  }

}
