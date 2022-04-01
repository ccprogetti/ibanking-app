import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IConto, Conto } from '../conto.model';
import { ContoService } from '../service/conto.service';

@Component({
  selector: 'jhi-conto-update',
  templateUrl: './conto-update.component.html',
})
export class ContoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nome: [null, [Validators.required]],
    iban: [null, [Validators.minLength(6)]],
    userName: [null, [Validators.required]],
    abi: [null, [Validators.required]],
  });

  constructor(protected contoService: ContoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conto }) => {
      this.updateForm(conto);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conto = this.createFromForm();
    if (conto.id !== undefined) {
      this.subscribeToSaveResponse(this.contoService.update(conto));
    } else {
      this.subscribeToSaveResponse(this.contoService.create(conto));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConto>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(conto: IConto): void {
    this.editForm.patchValue({
      id: conto.id,
      nome: conto.nome,
      iban: conto.iban,
      userName: conto.userName,
      abi: conto.abi,
    });
  }

  protected createFromForm(): IConto {
    return {
      ...new Conto(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
      iban: this.editForm.get(['iban'])!.value,
      userName: this.editForm.get(['userName'])!.value,
      abi: this.editForm.get(['abi'])!.value,
    };
  }
}
