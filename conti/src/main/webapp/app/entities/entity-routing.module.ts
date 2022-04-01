import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'conto',
        data: { pageTitle: 'contiApp.contiConto.home.title' },
        loadChildren: () => import('./conti/conto/conto.module').then(m => m.ContiContoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
