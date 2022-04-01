import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ContoDetailComponent } from './conto-detail.component';

describe('Conto Management Detail Component', () => {
  let comp: ContoDetailComponent;
  let fixture: ComponentFixture<ContoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ conto: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ContoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ContoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load conto on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.conto).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
