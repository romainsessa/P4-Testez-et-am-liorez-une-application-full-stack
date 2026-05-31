import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { FormComponent } from './form.component';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';

describe('FormComponent (integration)', () => {
  let fixture: ComponentFixture<FormComponent>;
  let component: FormComponent;
  let httpMock: HttpTestingController;

  const mockRouter = {
    url: '/create',
    navigate: jest.fn()
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
      token: '',
      type: '',
      username: '',
      firstName: '',
      lastName: ''
    }
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([]))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormComponent
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SessionApiService,
        { provide: SessionService, useValue: mockSessionService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create session (POST)', fakeAsync(() => {
    mockRouter.url = '/create';

    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'Yoga',
      date: '2024-01-01',
      teacher_id: 1,
      description: 'desc'
    });

    component.submit();

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.name).toBe('Yoga');

    req.flush({});

    tick();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  }));

  it('should update session (PUT)', fakeAsync(() => {
    mockRouter.url = '/update/1';

    fixture.detectChanges();

    const getReq = httpMock.expectOne('api/session/1');
    getReq.flush({
      id: 1,
      name: 'Yoga',
      date: new Date(),
      teacher_id: 1,
      description: 'desc'
    });

    tick();

    component.sessionForm?.setValue({
      name: 'Yoga updated',
      date: '2024-01-01',
      teacher_id: 1,
      description: 'desc'
    });

    component.submit();

    const putReq = httpMock.expectOne('api/session/1');
    expect(putReq.request.method).toBe('PUT');
    expect(putReq.request.body.name).toBe('Yoga updated');

    putReq.flush({});

    tick();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  }));

  it('should redirect if not admin', () => {
    const nonAdminService = {
      sessionInformation: { admin: false }
    };

    TestBed.resetTestingModule();

    TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SessionApiService,
        { provide: SessionService, useValue: nonAdminService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    const fixture2 = TestBed.createComponent(FormComponent);
    fixture2.detectChanges();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});