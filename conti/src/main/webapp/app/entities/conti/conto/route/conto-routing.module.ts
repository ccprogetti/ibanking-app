import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ContoComponent } from '../list/conto.component';
import { ContoDetailComponent } from '../detail/conto-detail.component';
import { ContoUpdateComponent } from '../update/conto-update.component';
import { ContoRoutingResolveService } from './conto-routing-resolve.service';

const contoRoute: Routes = [
  {
    path: '',
    component: ContoComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ContoDetailComponent,
    resolve: {
      conto: ContoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ContoUpdateComponent,
    resolve: {
      conto: ContoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ContoUpdateComponent,
    resolve: {
      conto: ContoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(contoRoute)],
  exports: [RouterModule],
})
export class ContoRoutingModule {}
