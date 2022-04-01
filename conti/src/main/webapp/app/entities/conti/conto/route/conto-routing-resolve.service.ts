import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConto, Conto } from '../conto.model';
import { ContoService } from '../service/conto.service';

@Injectable({ providedIn: 'root' })
export class ContoRoutingResolveService implements Resolve<IConto> {
  constructor(protected service: ContoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConto> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((conto: HttpResponse<Conto>) => {
          if (conto.body) {
            return of(conto.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Conto());
  }
}
