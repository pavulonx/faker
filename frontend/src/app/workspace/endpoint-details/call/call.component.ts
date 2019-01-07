import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-call',
  templateUrl: './call.component.html',
  styleUrls: ['./call.component.css']
})
export class CallComponent implements OnInit {

  @Input()
  call: Call;

  isCollapsed = true;

  constructor() {
  }

  ngOnInit() {
  }

  toggleCollapsed() {
    this.isCollapsed = !this.isCollapsed
  }

  stringify(call: Call) {
    return JSON.parse(JSON.stringify(call))
  }
}
