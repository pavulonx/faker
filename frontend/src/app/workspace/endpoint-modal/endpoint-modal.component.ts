import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {parseNumber} from '../../utils';

@Component({
  selector: 'app-endpoint-modal',
  templateUrl: './endpoint-modal.component.html',
  styleUrls: ['./endpoint-modal.component.css']
})
export class EndpointModalComponent implements OnInit {

  endpointForm: FormGroup;

  contentTypes: string[] = [
    "text/plain",
    "text/css",
    "text/csv",
    "text/html",
    "text/xml",
    "application/javascript",
    "application/octet-stream",
    "application/xhtml+xml",
    "application/json",
    "application/ld+json",
    "application/xml",
    "application/zip"
  ];

  constructor(public activeModal: NgbActiveModal, private formBuilder: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {
  }

  createForm() {
    this.endpointForm = this.formBuilder.group({
      ep_name: [''],
      ep_desc: [''],
      ep_res_code: [200, Validators.compose([Validators.required, Validators.max(550), Validators.min(200)])],
      ep_res_content: [''],
      ep_res_body: [''],
      ep_res_delay: ['0', Validators.max(10_000)]
    });
  }

  submitForm() {
    console.log(this.endpointForm.value);
    this.modal.close(this.toEndpoint(this.endpointForm.value));

  }

  toEndpoint(val): Endpoint {
    const res = {
      contentType: val.ep_res_content,
      code: val.ep_res_code,
      // headers: val.headers,
      body: val.ep_res_body,
      delay: parseNumber(val.ep_res_delay),
    };
    return {
      name: val.ep_name,
      description: val.ep_desc,
      responseTemplate: res
    };
  }

  dismissForm(cause: string) {
    this.modal.close({cause: cause});
  }

  get modal() {
    return this.activeModal;
  }
}
