import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-endpoint-modal',
  templateUrl: './endpoint-modal.component.html',
  styleUrls: ['./endpoint-modal.component.css']
})
export class EndpointModalComponent implements OnInit {

  @Input()id: number;
  myForm: FormGroup;

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder
  ) {
    this.createForm();
  }
  private createForm() {
    this.myForm = this.formBuilder.group({
      ep_name: '',
      ep_desc: '',
      ep_res_code: '',
      ep_res_content: '',
      ep_res_body: ''
    });
  }
  private submitForm() {
    this.activeModal.close(this.myForm.value);
  }
  ngOnInit() {
  }

  closeModal() {
    this.activeModal.close('Modal Closed');
  }

  get modal() {
    return this.activeModal;
  }
}
