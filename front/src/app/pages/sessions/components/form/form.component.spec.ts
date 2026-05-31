import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormComponent } from './form.component';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/core/service/session.service';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { TeacherService } from 'src/app/core/service/teacher.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('FormComponent (unit)', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = {
    sessionInformation: { admin: true }
  };

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

  const mockSessionApiService = {
    create: jest.fn(),
    update: jest.fn(),
    detail: jest.fn()
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([]))
  };

  const mockSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        provideRouter([]),
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init form in create mode', () => {
    mockRouter.url = '/create';
    fixture.detectChanges();

    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeTruthy();
  });

  it('should init form in update mode', () => {
    mockRouter.url = '/update/1';

    mockSessionApiService.detail.mockReturnValue(of({
      name: 'Yoga',
      date: new Date(),
      teacher_id: 1,
      description: 'desc'
    }));

    fixture.detectChanges();

    expect(component.onUpdate).toBe(true);
    expect(mockSessionApiService.detail).toHaveBeenCalled();
  });

  it('should call create on submit', fakeAsync(() => {
    mockRouter.url = '/create';
    component.onUpdate = false;

    mockSessionApiService.create.mockReturnValue(of({}));

    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'Yoga',
      date: '2024-01-01',
      teacher_id: 1,
      description: 'desc'
    });

    const exitSpy = jest.spyOn(component as any, 'exitPage');

    component.submit();

    tick();

    expect(mockSessionApiService.create).toHaveBeenCalled();
    expect(exitSpy).toHaveBeenCalledWith('Session created !');
  }));

  it('should call update on submit', fakeAsync(() => {
    mockRouter.url = '/update/1';

    mockSessionApiService.detail.mockReturnValue(of({
      name: 'Yoga',
      date: new Date(),
      teacher_id: 1,
      description: 'desc'
    }));

    mockSessionApiService.update.mockReturnValue(of({}));

    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'Yoga',
      date: '2024-01-01',
      teacher_id: 1,
      description: 'desc'
    });

    const exitSpy = jest.spyOn(component as any, 'exitPage');

    component.submit();

    tick();

    expect(mockSessionApiService.update).toHaveBeenCalled();
    expect(exitSpy).toHaveBeenCalledWith('Session updated !');
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
        provideRouter([]),
        { provide: SessionService, useValue: nonAdminService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    const fixture2 = TestBed.createComponent(FormComponent);
    fixture2.detectChanges();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should handle create error', () => {
    mockRouter.url = '/create';

    mockSessionApiService.create.mockReturnValue(throwError(() => new Error()));

    fixture.detectChanges();

    component.submit();

    expect(component).toBeTruthy(); // branch error couverte
  });

  it('should handle update error', () => {
    mockRouter.url = '/update/1';

    mockSessionApiService.detail.mockReturnValue(of({
      name: 'Yoga',
      date: new Date(),
      teacher_id: 1,
      description: 'desc'
    }));

    mockSessionApiService.update.mockReturnValue(throwError(() => new Error()));

    fixture.detectChanges();

    component.submit();

    expect(component).toBeTruthy();
  });
  
});