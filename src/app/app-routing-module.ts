import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './navbar/login/login';
import { CreateAccount } from './navbar/create-account/create-account';
import { VerifyOtp } from './navbar/verify-otp/verify-otp';
import { CustomerHomePage } from './pages/customer/customer-home-page/customer-home-page';

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
    component: CustomerHomePage
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
