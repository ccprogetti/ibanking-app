import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './bonifico.reducer';
import { IBonifico } from 'app/shared/model/bonifici/bonifico.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Bonifico = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const bonificoList = useAppSelector(state => state.bonifico.entities);
  const loading = useAppSelector(state => state.bonifico.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="bonifico-heading" data-cy="BonificoHeading">
        <Translate contentKey="gatewayApp.bonificiBonifico.home.title">Bonificos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.bonificiBonifico.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.bonificiBonifico.home.createLabel">Create new Bonifico</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {bonificoList && bonificoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.bonificiBonifico.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.bonificiBonifico.causale">Causale</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.bonificiBonifico.destinatario">Destinatario</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.bonificiBonifico.importo">Importo</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.bonificiBonifico.dataEsecuzione">Data Esecuzione</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.bonificiBonifico.ibanDestinatario">Iban Destinatario</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {bonificoList.map((bonifico, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${bonifico.id}`} color="link" size="sm">
                      {bonifico.id}
                    </Button>
                  </td>
                  <td>{bonifico.causale}</td>
                  <td>{bonifico.destinatario}</td>
                  <td>{bonifico.importo}</td>
                  <td>
                    {bonifico.dataEsecuzione ? (
                      <TextFormat type="date" value={bonifico.dataEsecuzione} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{bonifico.ibanDestinatario}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${bonifico.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${bonifico.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${bonifico.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gatewayApp.bonificiBonifico.home.notFound">No Bonificos found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Bonifico;
