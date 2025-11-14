import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Navbar } from './navbar/navbar/navbar';
import { Login } from './navbar/login/login';
import { CreateAccount } from './navbar/create-account/create-account';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';
import { VerifyOtp } from './navbar/verify-otp/verify-otp';
import { CustomerHomePage } from './pages/customer/customer-home-page/customer-home-page';
import { Product } from './pages/customer/product/product';
import { CustomerHomeHeroComponent } from './pages/customer/customer-home-hero-component/customer-home-hero-component';
import { Cart } from './pages/customer/cart/cart';

@NgModule({
  declarations: [
    App,
    Navbar,
    Login,
    CreateAccount,
    VerifyOtp,
    CustomerHomePage,
    Cart,
    Product,
    CustomerHomeHeroComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService
  ],
  bootstrap: [App]
})
export class AppModule { }
