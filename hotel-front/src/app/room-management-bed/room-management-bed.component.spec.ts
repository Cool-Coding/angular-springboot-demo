import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomManagementBedComponent } from './room-management-bed.component';

describe('RoomManagementBedComponent', () => {
  let component: RoomManagementBedComponent;
  let fixture: ComponentFixture<RoomManagementBedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoomManagementBedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoomManagementBedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
