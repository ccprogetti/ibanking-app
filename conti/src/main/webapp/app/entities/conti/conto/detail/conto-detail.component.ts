import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConto } from '../conto.model';

@Component({
  selector: 'jhi-conto-detail',
  templateUrl: './conto-detail.component.html',
})
export class ContoDetailComponent implements OnInit {
  conto: IConto | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conto }) => {
      this.conto = conto;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
