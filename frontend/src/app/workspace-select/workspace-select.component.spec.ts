import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspaceSelectComponent } from './workspace-select.component';

describe('WorkspaceSelectComponent', () => {
  let component: WorkspaceSelectComponent;
  let fixture: ComponentFixture<WorkspaceSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkspaceSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
