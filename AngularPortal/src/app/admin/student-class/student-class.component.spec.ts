import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentClassComponent } from './student-class.component';

describe('StudentClassComponent', () => {
  let component: StudentClassComponent;
  let fixture: ComponentFixture<StudentClassComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StudentClassComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentClassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
