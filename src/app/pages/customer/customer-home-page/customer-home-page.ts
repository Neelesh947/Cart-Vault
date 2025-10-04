import { Component } from '@angular/core';

@Component({
  selector: 'app-customer-home-page',
  standalone: false,
  templateUrl: './customer-home-page.html',
  styleUrl: './customer-home-page.css'
})
export class CustomerHomePage {

  username:string = ''

  logout(){}
}
