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

  constructor(public activeModal: NgbActiveModal, private formBuilder: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {
  }

  private createForm() {
    this.endpointForm = this.formBuilder.group({
      ep_name: [''],
      ep_desc: [''],
      ep_res_code: [200, Validators.required],
      ep_res_content: [''],
      ep_res_body: [''],
      ep_res_delay: ['']
    });
  }

  private submitForm() {
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

  private dismissForm(cause: string) {
    this.modal.close({cause: cause});
  }

  get modal() {
    return this.activeModal;
  }
}
