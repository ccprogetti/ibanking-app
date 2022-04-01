import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ContoService } from '../service/conto.service';
import { IConto, Conto } from '../conto.model';

import { ContoUpdateComponent } from './conto-update.component';

describe('Conto Management Update Component', () => {
  let comp: ContoUpdateComponent;
  let fixture: ComponentFixture<ContoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let contoService: ContoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ContoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ContoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ContoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    contoService = TestBed.inject(ContoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const conto: IConto = { id: 456 };

      activatedRoute.data = of({ conto });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(conto));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conto>>();
      const conto = { id: 123 };
      jest.spyOn(contoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conto }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(contoService.update).toHaveBeenCalledWith(conto);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conto>>();
      const conto = new Conto();
      jest.spyOn(contoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conto }));
      saveSubject.complete();

      // THEN
      expect(contoService.create).toHaveBeenCalledWith(conto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conto>>();
      const conto = { id: 123 };
      jest.spyOn(contoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(contoService.update).toHaveBeenCalledWith(conto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
