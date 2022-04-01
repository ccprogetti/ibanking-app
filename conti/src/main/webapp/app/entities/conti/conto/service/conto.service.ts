import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IConto, getContoIdentifier } from '../conto.model';

export type EntityResponseType = HttpResponse<IConto>;
export type EntityArrayResponseType = HttpResponse<IConto[]>;

@Injectable({ providedIn: 'root' })
export class ContoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/contos', 'conti');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(conto: IConto): Observable<EntityResponseType> {
    return this.http.post<IConto>(this.resourceUrl, conto, { observe: 'response' });
  }

  update(conto: IConto): Observable<EntityResponseType> {
    return this.http.put<IConto>(`${this.resourceUrl}/${getContoIdentifier(conto) as number}`, conto, { observe: 'response' });
  }

  partialUpdate(conto: IConto): Observable<EntityResponseType> {
    return this.http.patch<IConto>(`${this.resourceUrl}/${getContoIdentifier(conto) as number}`, conto, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IConto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IConto[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addContoToCollectionIfMissing(contoCollection: IConto[], ...contosToCheck: (IConto | null | undefined)[]): IConto[] {
    const contos: IConto[] = contosToCheck.filter(isPresent);
    if (contos.length > 0) {
      const contoCollectionIdentifiers = contoCollection.map(contoItem => getContoIdentifier(contoItem)!);
      const contosToAdd = contos.filter(contoItem => {
        const contoIdentifier = getContoIdentifier(contoItem);
        if (contoIdentifier == null || contoCollectionIdentifiers.includes(contoIdentifier)) {
          return false;
        }
        contoCollectionIdentifiers.push(contoIdentifier);
        return true;
      });
      return [...contosToAdd, ...contoCollection];
    }
    return contoCollection;
  }
}
