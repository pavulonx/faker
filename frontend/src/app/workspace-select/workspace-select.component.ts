import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ApiService} from "../api.service";
import {delay, map} from "rxjs/operators";

@Component({
  selector: 'app-workspace-select',
  templateUrl: './workspace-select.component.html',
  styleUrls: ['./workspace-select.component.css']
})
export class WorkspaceSelectComponent implements OnInit {

  workspaceSelect: FormGroup;

  constructor(private formBuilder: FormBuilder, private router: Router, private api: ApiService) {
    this.createForm();
  }

  ngOnInit() {
  }

  createForm() {
    this.workspaceSelect = this.formBuilder.group({
      workspace_select_name: ['', Validators.required, this.validateExists.bind(this)]
    });
  }

  validateExists(control: AbstractControl) {
    return this.api.getWorkspace(control.value).pipe(map(res => res ? null : {validateExists: true}));
  }

  submitForm() {
    this.router.navigate(['workspace/' + this.workspaceSelect.value.workspace_select_name]);
  }

  createWorkspace() {
    const input = this.workspaceSelect.value.workspace_select_name;
    this.api.addWorkspace({name: input})
      .pipe(delay(1000))
      .subscribe(
      e => this.router.navigate(['workspace/' + input]),
      err => this.router.navigate(['workspace/'])
    );
  }

}
