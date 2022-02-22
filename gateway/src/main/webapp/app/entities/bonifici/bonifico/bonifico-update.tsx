import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './bonifico.reducer';
import { IBonifico } from 'app/shared/model/bonifici/bonifico.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BonificoUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const bonificoEntity = useAppSelector(state => state.bonifico.entity);
  const loading = useAppSelector(state => state.bonifico.loading);
  const updating = useAppSelector(state => state.bonifico.updating);
  const updateSuccess = useAppSelector(state => state.bonifico.updateSuccess);
  const handleClose = () => {
    props.history.push('/bonifico');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...bonificoEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...bonificoEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.bonificiBonifico.home.createOrEditLabel" data-cy="BonificoCreateUpdateHeading">
            <Translate contentKey="gatewayApp.bonificiBonifico.home.createOrEditLabel">Create or edit a Bonifico</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="bonifico-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.bonificiBonifico.causale')}
                id="bonifico-causale"
                name="causale"
                data-cy="causale"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 5, message: translate('entity.validation.minlength', { min: 5 }) },
                }}
              />
              <ValidatedField
                label={translate('gatewayApp.bonificiBonifico.destinatario')}
                id="bonifico-destinatario"
                name="destinatario"
                data-cy="destinatario"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 10, message: translate('entity.validation.minlength', { min: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('gatewayApp.bonificiBonifico.importo')}
                id="bonifico-importo"
                name="importo"
                data-cy="importo"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.bonificiBonifico.dataEsecuzione')}
                id="bonifico-dataEsecuzione"
                name="dataEsecuzione"
                data-cy="dataEsecuzione"
                type="date"
              />
              <ValidatedField
                label={translate('gatewayApp.bonificiBonifico.ibanDestinatario')}
                id="bonifico-ibanDestinatario"
                name="ibanDestinatario"
                data-cy="ibanDestinatario"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/bonifico" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default BonificoUpdate;
