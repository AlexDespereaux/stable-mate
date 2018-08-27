import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayClassComponent } from './display-class.component';

describe('DisplayClassComponent', () => {
  let component: DisplayClassComponent;
  let fixture: ComponentFixture<DisplayClassComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DisplayClassComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DisplayClassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
