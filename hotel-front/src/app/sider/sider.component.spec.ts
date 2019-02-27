import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SiderComponent } from './sider.component';

describe('SiderComponent', () => {
  let component: SiderComponent;
  let fixture: ComponentFixture<SiderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SiderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SiderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
