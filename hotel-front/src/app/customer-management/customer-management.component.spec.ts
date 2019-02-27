import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerManagementComponent } from './customer-management.component';

describe('CustomerManagementComponent', () => {
  let component: CustomerManagementComponent;
  let fixture: ComponentFixture<CustomerManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
