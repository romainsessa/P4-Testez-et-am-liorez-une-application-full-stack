import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

describe('DetailComponent (unit)', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  const mockSession = {
    id: 1,
    name: 'Yoga',
    users: [1],
    teacher_id: 2,
    description: 'desc',
    date: new Date(),
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    delete: jest.fn().mockReturnValue(of({})),
    participate: jest.fn().mockReturnValue(of({})),
    unParticipate: jest.fn().mockReturnValue(of({}))
  };

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of({
      firstName: 'John',
      lastName: 'Doe'
    }))
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: () => '1'
      }
    }
  };

  const mockRouter = {
    navigate: jest.fn()
  };

  const mockSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DetailComponent,
        RouterTestingModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load session and teacher', () => {
    expect(mockSessionApiService.detail).toHaveBeenCalled();
    expect(mockTeacherService.detail).toHaveBeenCalled();
    expect(component.session?.name).toBe('Yoga');
  });

  it('should set isParticipate true', () => {
    expect(component.isParticipate).toBe(true);
  });

  it('should delete session', () => {
    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should participate', () => {
    component.participate();

    expect(mockSessionApiService.participate).toHaveBeenCalled();
  });

  it('should unParticipate', () => {
    component.unParticipate();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalled();
  });


  it('should set isParticipate false', () => {
    mockSessionApiService.detail.mockReturnValue(of({
      ...mockSession,
      users: []
    }));

    const newFixture = TestBed.createComponent(DetailComponent);
    const newComp = newFixture.componentInstance as DetailComponent;
    newFixture.detectChanges();

    expect(newComp.isParticipate).toBe(false);
  });

});