import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-workspace-select',
  templateUrl: './workspace-select.component.html',
  styleUrls: ['./workspace-select.component.css']
})
export class WorkspaceSelectComponent implements OnInit {

  workspaceSelect: FormGroup;

  constructor(private formBuilder: FormBuilder, private router: Router) {
    this.createForm();
  }

  ngOnInit() {
  }

  private createForm() {
    this.workspaceSelect = this.formBuilder.group({
      workspace_select_name: ['', Validators.required],
    });
  }

  private submitForm() {
    this.router.navigate(['workspace/' + this.workspaceSelect.value.workspace_select_name]);
  }

}
