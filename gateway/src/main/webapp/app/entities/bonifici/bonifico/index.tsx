import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Bonifico from './bonifico';
import BonificoDetail from './bonifico-detail';
import BonificoUpdate from './bonifico-update';
import BonificoDeleteDialog from './bonifico-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BonificoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BonificoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BonificoDetail} />
      <ErrorBoundaryRoute path={match.url} component={Bonifico} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BonificoDeleteDialog} />
  </>
);

export default Routes;
