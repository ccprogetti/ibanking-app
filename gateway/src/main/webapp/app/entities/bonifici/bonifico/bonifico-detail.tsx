import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './bonifico.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BonificoDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const bonificoEntity = useAppSelector(state => state.bonifico.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bonificoDetailsHeading">
          <Translate contentKey="gatewayApp.bonificiBonifico.detail.title">Bonifico</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{bonificoEntity.id}</dd>
          <dt>
            <span id="causale">
              <Translate contentKey="gatewayApp.bonificiBonifico.causale">Causale</Translate>
            </span>
          </dt>
          <dd>{bonificoEntity.causale}</dd>
          <dt>
            <span id="destinatario">
              <Translate contentKey="gatewayApp.bonificiBonifico.destinatario">Destinatario</Translate>
            </span>
          </dt>
          <dd>{bonificoEntity.destinatario}</dd>
          <dt>
            <span id="importo">
              <Translate contentKey="gatewayApp.bonificiBonifico.importo">Importo</Translate>
            </span>
          </dt>
          <dd>{bonificoEntity.importo}</dd>
          <dt>
            <span id="dataEsecuzione">
              <Translate contentKey="gatewayApp.bonificiBonifico.dataEsecuzione">Data Esecuzione</Translate>
            </span>
          </dt>
          <dd>
            {bonificoEntity.dataEsecuzione ? (
              <TextFormat value={bonificoEntity.dataEsecuzione} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="ibanDestinatario">
              <Translate contentKey="gatewayApp.bonificiBonifico.ibanDestinatario">Iban Destinatario</Translate>
            </span>
          </dt>
          <dd>{bonificoEntity.ibanDestinatario}</dd>
        </dl>
        <Button tag={Link} to="/bonifico" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/bonifico/${bonificoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BonificoDetail;
