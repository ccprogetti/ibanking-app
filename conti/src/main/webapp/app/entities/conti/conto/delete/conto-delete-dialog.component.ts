import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IConto } from '../conto.model';
import { ContoService } from '../service/conto.service';

@Component({
  templateUrl: './conto-delete-dialog.component.html',
})
export class ContoDeleteDialogComponent {
  conto?: IConto;

  constructor(protected contoService: ContoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.contoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
