import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomNoSearchComponent } from './room-no-search.component';

describe('RoomNoSearchComponent', () => {
  let component: RoomNoSearchComponent;
  let fixture: ComponentFixture<RoomNoSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoomNoSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoomNoSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
