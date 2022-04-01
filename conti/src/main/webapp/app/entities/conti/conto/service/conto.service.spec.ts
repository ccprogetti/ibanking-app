import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IConto, Conto } from '../conto.model';

import { ContoService } from './conto.service';

describe('Conto Service', () => {
  let service: ContoService;
  let httpMock: HttpTestingController;
  let elemDefault: IConto;
  let expectedResult: IConto | IConto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ContoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nome: 'AAAAAAA',
      iban: 'AAAAAAA',
      userName: 'AAAAAAA',
      abi: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Conto', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Conto()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Conto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
          iban: 'BBBBBB',
          userName: 'BBBBBB',
          abi: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Conto', () => {
      const patchObject = Object.assign(
        {
          nome: 'BBBBBB',
          iban: 'BBBBBB',
        },
        new Conto()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Conto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
          iban: 'BBBBBB',
          userName: 'BBBBBB',
          abi: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Conto', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addContoToCollectionIfMissing', () => {
      it('should add a Conto to an empty array', () => {
        const conto: IConto = { id: 123 };
        expectedResult = service.addContoToCollectionIfMissing([], conto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conto);
      });

      it('should not add a Conto to an array that contains it', () => {
        const conto: IConto = { id: 123 };
        const contoCollection: IConto[] = [
          {
            ...conto,
          },
          { id: 456 },
        ];
        expectedResult = service.addContoToCollectionIfMissing(contoCollection, conto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Conto to an array that doesn't contain it", () => {
        const conto: IConto = { id: 123 };
        const contoCollection: IConto[] = [{ id: 456 }];
        expectedResult = service.addContoToCollectionIfMissing(contoCollection, conto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conto);
      });

      it('should add only unique Conto to an array', () => {
        const contoArray: IConto[] = [{ id: 123 }, { id: 456 }, { id: 31778 }];
        const contoCollection: IConto[] = [{ id: 123 }];
        expectedResult = service.addContoToCollectionIfMissing(contoCollection, ...contoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conto: IConto = { id: 123 };
        const conto2: IConto = { id: 456 };
        expectedResult = service.addContoToCollectionIfMissing([], conto, conto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conto);
        expect(expectedResult).toContain(conto2);
      });

      it('should accept null and undefined values', () => {
        const conto: IConto = { id: 123 };
        expectedResult = service.addContoToCollectionIfMissing([], null, conto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conto);
      });

      it('should return initial array if no Conto is added', () => {
        const contoCollection: IConto[] = [{ id: 123 }];
        expectedResult = service.addContoToCollectionIfMissing(contoCollection, undefined, null);
        expect(expectedResult).toEqual(contoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
