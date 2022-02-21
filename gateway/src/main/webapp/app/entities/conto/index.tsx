import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Conto from './conto';
import ContoDetail from './conto-detail';
import ContoUpdate from './conto-update';
import ContoDeleteDialog from './conto-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ContoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ContoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ContoDetail} />
      <ErrorBoundaryRoute path={match.url} component={Conto} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ContoDeleteDialog} />
  </>
);

export default Routes;
