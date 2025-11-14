import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './navbar/login/login';
import { CreateAccount } from './navbar/create-account/create-account';
import { VerifyOtp } from './navbar/verify-otp/verify-otp';
import { CustomerHomePage } from './pages/customer/customer-home-page/customer-home-page';
import { Cart } from './pages/customer/cart/cart';
import { CustomerHomeHeroComponent } from './pages/customer/customer-home-hero-component/customer-home-hero-component';
import { Product } from './pages/customer/product/product';

const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'create-account',
    component: CreateAccount
  },
  {
    path: 'verify-otp/:userId',
    component: VerifyOtp
  },
  {
    path: 'customer-home-page',
    component: CustomerHomePage,
    children: [
      { path: 'cart', component: Cart },
      { path: '', component: CustomerHomeHeroComponent },
    ]
  },
  { path: 'products', component: Product }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
