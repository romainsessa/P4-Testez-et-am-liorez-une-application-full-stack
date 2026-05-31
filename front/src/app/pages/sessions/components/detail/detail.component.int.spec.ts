import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

describe('DetailComponent (integration)', () => {
  let fixture: ComponentFixture<DetailComponent>;
  let component: DetailComponent;
  let httpMock: HttpTestingController;

  const mockRouter = {
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
      id: 1,
      admin: true,
      token: '',
      type: '',
      username: '',
      firstName: '',
      lastName: ''
    }
  };

  const mockSession = {
    id: 1,
    name: 'Yoga',
    date: new Date(),
    teacher_id: 2,
    description: 'desc',
    users: [1],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeacher = {
    id: 2,
    firstName: 'John',
    lastName: 'Doe'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DetailComponent
      ],
      providers: [
        SessionApiService,
        TeacherService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should load session and teacher', fakeAsync(() => {
    fixture.detectChanges();

    const sessionReq = httpMock.expectOne('api/session/1');
    sessionReq.flush(mockSession);

    const teacherReq = httpMock.expectOne('api/teacher/2');
    teacherReq.flush(mockTeacher);

    tick();

    expect(component.session?.name).toBe('Yoga');
    expect(component.teacher?.firstName).toBe('John');
    expect(component.isParticipate).toBe(true);
  }));

  it('should delete session', fakeAsync(() => {
    fixture.detectChanges();

    const sessionReq = httpMock.expectOne('api/session/1');
    sessionReq.flush(mockSession);

    const teacherReq = httpMock.expectOne('api/teacher/2');
    teacherReq.flush(mockTeacher);

    tick();

    component.delete();

    const deleteReq = httpMock.expectOne('api/session/1');
    expect(deleteReq.request.method).toBe('DELETE');

    deleteReq.flush(null);

    tick();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  }));

  it('should participate', fakeAsync(() => {
    fixture.detectChanges();

    // load initial
    httpMock.expectOne('api/session/1').flush(mockSession);
    httpMock.expectOne('api/teacher/2').flush(mockTeacher);

    tick();

    component.participate();

    const partReq = httpMock.expectOne('api/session/1/participate/1');
    expect(partReq.request.method).toBe('POST');

    partReq.flush(null);

    // fetchSession again
    httpMock.expectOne('api/session/1').flush(mockSession);
    httpMock.expectOne('api/teacher/2').flush(mockTeacher);

    tick();
  }));

  it('should unParticipate', fakeAsync(() => {
    fixture.detectChanges();

    httpMock.expectOne('api/session/1').flush(mockSession);
    httpMock.expectOne('api/teacher/2').flush(mockTeacher);

    tick();

    component.unParticipate();

    const unPartReq = httpMock.expectOne('api/session/1/participate/1');
    expect(unPartReq.request.method).toBe('DELETE');

    unPartReq.flush(null);

    // fetchSession again
    httpMock.expectOne('api/session/1').flush(mockSession);
    httpMock.expectOne('api/teacher/2').flush(mockTeacher);

    tick();
  }));
});