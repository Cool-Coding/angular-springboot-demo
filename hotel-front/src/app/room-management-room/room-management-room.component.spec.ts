import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomManagementRoomComponent } from './room-management-room.component';

describe('RoomManagementRoomComponent', () => {
  let component: RoomManagementRoomComponent;
  let fixture: ComponentFixture<RoomManagementRoomComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoomManagementRoomComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoomManagementRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
