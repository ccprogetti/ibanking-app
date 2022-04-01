import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ContoComponent } from './list/conto.component';
import { ContoDetailComponent } from './detail/conto-detail.component';
import { ContoUpdateComponent } from './update/conto-update.component';
import { ContoDeleteDialogComponent } from './delete/conto-delete-dialog.component';
import { ContoRoutingModule } from './route/conto-routing.module';

@NgModule({
  imports: [SharedModule, ContoRoutingModule],
  declarations: [ContoComponent, ContoDetailComponent, ContoUpdateComponent, ContoDeleteDialogComponent],
  entryComponents: [ContoDeleteDialogComponent],
})
export class ContiContoModule {}
