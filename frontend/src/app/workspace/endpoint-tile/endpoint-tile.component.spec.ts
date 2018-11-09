import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EndpointTileComponent } from './endpoint-tile.component';

describe('EndpointTileComponent', () => {
  let component: EndpointTileComponent;
  let fixture: ComponentFixture<EndpointTileComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EndpointTileComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EndpointTileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
