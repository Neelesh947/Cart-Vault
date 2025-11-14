import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerHomeHeroComponent } from './customer-home-hero-component';

describe('CustomerHomeHeroComponent', () => {
  let component: CustomerHomeHeroComponent;
  let fixture: ComponentFixture<CustomerHomeHeroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomerHomeHeroComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerHomeHeroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
